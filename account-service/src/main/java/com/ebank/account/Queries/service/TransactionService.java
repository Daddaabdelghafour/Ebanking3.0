// TransactionService.java - UPDATED with Kafka notifications
package com.ebank.account.Queries.service;

import com.ebank.account.Commands.dto.AddBeneficiaryRequestDTO;
import com.ebank.account.Commands.dto.ConfirmTransactionRequestDTO;
import com.ebank.account.Commands.dto.InitiateTransactionRequestDTO;
import com.ebank.account.Commands.exception.AccountNotActivatedException;
import com.ebank.account.Commands.exception.BeneficiaryAlreadyExistsException;
import com.ebank.account.Commands.exception.InsufficientBalanceException;
import com.ebank.account.Commands.exception.InvalidOtpException;
import com.ebank.account.Common.enums.AccountStatus;
import com.ebank.account.Common.enums.OperationType;
import com.ebank.account.Common.enums.TransactionStatus;
import com.ebank.account.Common.enums.TransactionType;
import com.ebank.account.Common.kafka.dto.NotificationEventDTO;
import com.ebank.account.Common.kafka.producer.NotificationEventProducer;
import com.ebank.account.Queries.dto.BeneficiaryResponseDTO;
import com.ebank.account.Queries.dto.TransactionResponseDTO;
import com.ebank.account.Queries.entity.Account;
import com.ebank.account.Queries.entity.Beneficiary;
import com.ebank.account.Queries.entity.Operation;
import com.ebank.account.Queries.entity.Transaction;
import com.ebank.account.Queries.exception.AccountNotFoundException;
import com.ebank.account.Queries.exception.BeneficiaryNotFoundException;
import com.ebank.account.Queries.exception.TransactionNotFoundException;
import com.ebank.account.Queries.mapper.EntityMapper;
import com.ebank.account.Queries.repository.AccountRepository;
import com.ebank.account.Queries.repository.BeneficiaryRepository;
import com.ebank.account.Queries.repository.OperationRepository;
import com.ebank.account.Queries.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final OperationRepository operationRepository;
    private final EntityMapper entityMapper;
    private final NotificationEventProducer notificationProducer;

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final BigDecimal MIN_TRANSFER_AMOUNT = new BigDecimal("1.00");
    private static final BigDecimal MAX_TRANSFER_AMOUNT = new BigDecimal(
            "50000.00");request.sourceAccountId(),request.beneficiaryId());

    // Validate transaction amount
    if(request.amount().compareTo(MIN_TRANSFER_AMOUNT)<0)
    {
        throw new IllegalArgumentException("Amount must be at least " + MIN_TRANSFER_AMOUNT + " MAD");
    }if(request.amount().compareTo(MAX_TRANSFER_AMOUNT)>0)
    {
        throw new IllegalArgumentException("Amount exceeds maximum limit of " + MAX_TRANSFER_AMOUNT + " MAD");
    }

    // Validate source account
    Account sourceAccount = accountRepository.findById(request.sourceAccountId())
            .orElseThrow(() -> new AccountNotFoundException(request.sourceAccountId()));

    // Check source account status
    if(sourceAccount.getStatus()!=AccountStatus.ACTIVATED)
    {
        throw new AccountNotActivatedException(sourceAccount.getId());
    }

    // Validate beneficiary
    Beneficiary beneficiary = beneficiaryRepository.findByIdAndAccountId(
            request.beneficiaryId(), request.sourceAccountId())
            .orElseThrow(() -> new BeneficiaryNotFoundException(request.beneficiaryId()));

    // Validate destination account using beneficiary's RIB/IBAN - FIXED: Use proper
    // query
    Account destinationAccount = accountRepository.findByIban(beneficiary.getBeneficiaryRib())
            .orElseThrow(() -> new AccountNotFoundException(
                    "Destination account not found for RIB: " + beneficiary.getBeneficiaryRib()));

    // Check destination account status
    if(destinationAccount.getStatus()!=AccountStatus.ACTIVATED)
    {
        throw new AccountNotActivatedException("Destination account is not activated");
    }

    // Prevent self-transfer
    if(sourceAccount.getId().equals(destinationAccount.getId()))
    {
        throw new IllegalArgumentException("Cannot transfer to the same account");
    }

    // Generate OTP
    String otpCode = generateOtp();

    // Create transaction
    Transaction transaction = Transaction.builder()
            .sourceAccount(sourceAccount)
            .destinationAccount(destinationAccount)
            .amount(request.amount())
            .reference(request.reference())
            .type(TransactionType.TRANSFER)
            .status(TransactionStatus.OTP_SENT)
            .otpCode(otpCode)
            .otpExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
            .otpVerified(false)
            .build();

    transaction=transactionRepository.save(transaction);

    // Send OTP notification via Kafka
    sendOtpNotification(sourceAccount, transaction, otpCode, beneficiary);
        
        log.info("Transaction {} initiated successfully, OTP sent", transaction.getId());

        return entityMapper.toTransactionResponseDTO(transaction);
    }

    @Transactional
    public TransactionResponseDTO confirmTransaction(ConfirmTransactionRequestDTO request) {
        log.info("Confirming transaction {}", request.transactionId());

        Transaction transaction = transactionRepository.findById(request.transactionId())
                .orElseThrow(() -> new TransactionNotFoundException(request.transactionId()));
        
        // Validate transaction is in correct state
        if (transaction.getStatus() != TransactionStatus.OTP_SENT) {
            throw new InvalidOtpException("Transaction is not in OTP_SENT state. Current status: " + transaction.getStatus());
        }

        // Validate OTP
        if (!transaction.getOtpCode().equals(request.otpCode())) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason("Invalid OTP code");
            transactionRepository.save(transaction);

            // Send failure notification
            sendTransactionFailedNotification(transaction, "Invalid OTP code");

            throw new InvalidOtpException("Invalid OTP code");
        }

        // Check OTP expiry
        if (LocalDateTime.now().isAfter(transaction.getOtpExpiryTime())) {
            transaction.setStatus(TransactionStatus.EXPIRED);
            transaction.setFailureReason("OTP has expired");
            transactionRepository.save(transaction);

            // Send failure notification
            sendTransactionFailedNotification(transaction, "OTP has expired");

            throw new InvalidOtpException("OTP has expired");
        }

        // Check if already verified
        if (Boolean.TRUE.equals(transaction.getOtpVerified())) {
            throw new InvalidOtpException("Transaction already verified");
        }

        transaction.setOtpVerified(true);
        transaction.setStatus(TransactionStatus.PROCESSING);

        try {
            // Execute the transaction
            Account sourceAccount = transaction.getSourceAccount();
            Account destinationAccount = transaction.getDestinationAccount();

            // Debit source account
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));

            // Credit destination account
            destinationAccount.setBalance(destinationAccount.getBalance().add(transaction.getAmount()));

            // Save accounts
            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            // Create operation for source account (DEBIT)
            Operation debitOperation = Operation.builder()
                    .account(sourceAccount)
                    .type(OperationType.DEBIT)
                    .amount(transaction.getAmount())
                    .description(String.format("Transfer to %s - Ref: %s",
                            destinationAccount.getAccountNumber(),
                            transaction.getReference()))
                    .build();
            operationRepository.save(debitOperation);

            // Create operation for destination account (CREDIT)
            Operation creditOperation = Operation.builder()
                    .account(destinationAccount)
                    .type(OperationType.CREDIT)
                    .amount(transaction.getAmount())
                    .description(String.format("Transfer from %s - Ref: %s",
                            sourceAccount.getAccountNumber(),
                            transaction.getReference()))
                    .build();
            operationRepository.save(creditOperation);

            // Update transaction status
            transaction.setStatus(TransactionStatus.COMPLETED);

            // Send success notifications to both parties
            sendTransactionSuccessNotification(transaction);

            log.info("Transaction {} completed successfully with operations created", transaction.getId());

        } catch (Exception e) {
            log.error("Failed to process transaction {}: {}", transaction.getId(), e.getMessage(), e);
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason("Transaction processing failed: " + e.getMessage());
            transactionRepository.save(transaction);

            // Send failure notification
            sendTransactionFailedNotification(transaction, e.getMessage());
            
            // Re-throw to rollback the transaction
            throw new RuntimeException("Failed to complete transaction: " + e.getMessage(), e);
        }

        transaction = transactionRepository.save(transaction);
        return entityMapper.toTransactionResponseDTO(transaction);
    }

    @Transactional
    public BeneficiaryResponseDTO addBeneficiary(AddBeneficiaryRequestDTO request) {
        log.info("Adding beneficiary for account {}", request.accountId());

        // Validate account
        Account destinationAccount = accountRepository.findByRibOrIban(request.beneficiaryRib())
        .orElseThrow(() -> new AccountNotFoundException(
                "Destination account not found for RIB/IBAN: " + request.beneficiaryRib()));

        // Check if beneficiary already exists
        beneficiaryRepository.findByAccountIdAndRib(request.accountId(), request.beneficiaryRib())
                .ifPresent(b -> {
                    throw new BeneficiaryAlreadyExistsException(request.beneficiaryRib());
                });

        // Create beneficiary
        Beneficiary beneficiary = Beneficiary.builder()
                .account(account)
                .beneficiaryName(request.beneficiaryName())
                .beneficiaryRib(request.beneficiaryRib())
                .isActive(true)
                .build();

        beneficiary = beneficiaryRepository.save(beneficiary);

        // Send beneficiary added notification
        sendBeneficiaryAddedNotification(account, beneficiary);

        log.info("Beneficiary {} added successfully", beneficiary.getId());

        return entityMapper.toBeneficiaryResponseDTO(beneficiary);
    }

    @Transactional
    public void deleteBeneficiary(UUID beneficiaryId, UUID accountId) {
        log.info("Deleting beneficiary {} for account {}", beneficiaryId, accountId);

        Beneficiary beneficiary = beneficiaryRepository.findByIdAndAccountId(beneficiaryId, accountId)
                .orElseThrow(() -> new BeneficiaryNotFoundException(beneficiaryId));

        beneficiary.setDeletedAt(LocalDateTime.now());
        beneficiary.setIsActive(false);
        beneficiaryRepository.save(beneficiary);

        log.info("Beneficiary {} deleted successfully", beneficiaryId);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Method to clean up expired transactions (can be called by a scheduled job)
    @Transactional
    public void expireOldTransactions() {
        log.info("Checking for expired OTP transactions");

        List<Transaction> expiredTransactions = transactionRepository
                .findExpiredOtpTransactions(LocalDateTime.now());

        expiredTransactions.forEach(transaction -> {
            transaction.setStatus(TransactionStatus.EXPIRED);
            transaction.setFailureReason("OTP expired");
        });

        if (!expiredTransactions.isEmpty()) {
            transactionRepository.saveAll(expiredTransactions);
            log.info("Expired {} transactions", expiredTransactions.size());
        }
    }

    // ==================== Notification Methods ====================

    private void sendOtpNotification(Account account, Transaction transaction, String otpCode,
            Beneficiary beneficiary) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("transactionId", transaction.getId().toString());
            metadata.put("amount", transaction.getAmount().toString());
            metadata.put("currency", "MAD");
            metadata.put("beneficiaryName", beneficiary.getBeneficiaryName());
            metadata.put("beneficiaryRib", beneficiary.getBeneficiaryRib());
            metadata.put("reference", transaction.getReference());
            metadata.put("expiryMinutes", OTP_EXPIRY_MINUTES);

            NotificationEventDTO notification = NotificationEventDTO.builder()
                    .notificationId(UUID.randomUUID())
                    .recipient(account.getEmail())
                    .channel("TRANSACTION_OTP")
                    .subject("Transaction OTP Verification")
                    .message(String.format("Your OTP code is: %s. Valid for %d minutes.", otpCode, OTP_EXPIRY_MINUTES))
                    .metadata(metadata)
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationProducer.sendOtpNotification(notification);
            log.info("OTP notification sent for transaction {}", transaction.getId());

        } catch (Exception e) {
            log.error("Failed to send OTP notification: {}", e.getMessage(), e);
            // Don't throw exception - notification failure shouldn't block transaction
            // initiation
        }
    }

    private void sendTransactionSuccessNotification(Transaction transaction) {
        try {
            Account sourceAccount = transaction.getSourceAccount();
            Account destinationAccount = transaction.getDestinationAccount();

            // Notification for sender
            Map<String, Object> senderMetadata = new HashMap<>();
            senderMetadata.put("transactionId", transaction.getId().toString());
            senderMetadata.put("amount", transaction.getAmount().toString());
            senderMetadata.put("currency", "MAD");
            senderMetadata.put("destinationAccount", destinationAccount.getAccountNumber());
            senderMetadata.put("reference", transaction.getReference());
            senderMetadata.put("newBalance", sourceAccount.getBalance().toString());
            senderMetadata.put("transactionType", "DEBIT");

            NotificationEventDTO senderNotification = NotificationEventDTO.builder()
                    .notificationId(UUID.randomUUID())
                    .recipient(sourceAccount.getEmail())
                    .channel("TRANSACTION_SUCCESS")
                    .subject("Transaction Completed Successfully")
                    .message(String.format("Your transfer of %.2f MAD to account %s has been completed successfully.",
                            transaction.getAmount(), destinationAccount.getAccountNumber()))
                    .metadata(senderMetadata)
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationProducer.sendTransactionSuccessNotification(senderNotification);

            // Notification for receiver
            Map<String, Object> receiverMetadata = new HashMap<>();
            receiverMetadata.put("transactionId", transaction.getId().toString());
            receiverMetadata.put("amount", transaction.getAmount().toString());
            receiverMetadata.put("currency", "MAD");
            receiverMetadata.put("sourceAccount", sourceAccount.getAccountNumber());
            receiverMetadata.put("reference", transaction.getReference());
            receiverMetadata.put("newBalance", destinationAccount.getBalance().toString());
            receiverMetadata.put("transactionType", "CREDIT");

            NotificationEventDTO receiverNotification = NotificationEventDTO.builder()
                    .notificationId(UUID.randomUUID())
                    .recipient(destinationAccount.getEmail())
                    .channel("TRANSACTION_SUCCESS")
                    .subject("You Received a Transfer")
                    .message(String.format("You have received %.2f MAD from account %s.",
                            transaction.getAmount(), sourceAccount.getAccountNumber()))
                    .metadata(receiverMetadata)
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationProducer.sendTransactionSuccessNotification(receiverNotification);

            log.info("Success notifications sent for transaction {}", transaction.getId());

        } catch (Exception e) {
            log.error("Failed to send success notifications: {}", e.getMessage(), e);
        }
    }

    private void sendTransactionFailedNotification(Transaction transaction, String reason) {
        try {
            Account sourceAccount = transaction.getSourceAccount();

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("transactionId", transaction.getId().toString());
            metadata.put("amount", transaction.getAmount().toString());
            metadata.put("currency", "MAD");
            metadata.put("reference", transaction.getReference());
            metadata.put("failureReason", reason);

            NotificationEventDTO notification = NotificationEventDTO.builder()
                    .notificationId(UUID.randomUUID())
                    .recipient(sourceAccount.getEmail())
                    .channel("TRANSACTION_FAILED")
                    .subject("Transaction Failed")
                    .message(String.format("Your transaction of %.2f MAD has failed. Reason: %s",
                            transaction.getAmount(), reason))
                    .metadata(metadata)
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationProducer.sendTransactionFailedNotification(notification);
            log.info("Failure notification sent for transaction {}", transaction.getId());

        } catch (Exception e) {
            log.error("Failed to send failure notification: {}", e.getMessage(), e);
        }
    }

    private void sendBeneficiaryAddedNotification(Account account, Beneficiary beneficiary) {
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("beneficiaryId", beneficiary.getId().toString());
            metadata.put("beneficiaryName", beneficiary.getBeneficiaryName());
            metadata.put("beneficiaryRib", beneficiary.getBeneficiaryRib());
            metadata.put("accountId", account.getId().toString());

            NotificationEventDTO notification = NotificationEventDTO.builder()
                    .notificationId(UUID.randomUUID())
                    .recipient(account.getEmail())
                    .channel("BENEFICIARY_ADDED")
                    .subject("New Beneficiary Added")
                    .message(String.format("You have successfully added %s (%s) as a beneficiary.",
                            beneficiary.getBeneficiaryName(), beneficiary.getBeneficiaryRib()))
                    .metadata(metadata)
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationProducer.sendBeneficiaryAddedNotification(notification);
            log.info("Beneficiary added notification sent for beneficiary {}", beneficiary.getId());

        } catch (Exception e) {
            log.error("Failed to send beneficiary added notification: {}", e.getMessage(), e);
        }
    }
}