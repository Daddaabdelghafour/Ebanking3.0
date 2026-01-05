package com.ebank.account.Queries.service;

import com.ebank.account.Common.enums.AccountStatus;
import com.ebank.account.Common.enums.OperationType;
import com.ebank.account.Common.event.*;
import com.ebank.account.Common.kafka.dto.AccountEventDTO;
import com.ebank.account.Common.kafka.producer.AccountEventProducer;
import com.ebank.account.Common.service.RibGeneratorService;
import com.ebank.account.Queries.entity.Account;
import com.ebank.account.Queries.entity.Operation;
import com.ebank.account.Queries.exception.AccountNotFoundException;
import com.ebank.account.Queries.repository.AccountRepository;
import com.ebank.account.Queries.repository.OperationRepository;
import com.ebank.account.Queries.service.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@ProcessingGroup("account")
public class AccountEventHandlerService {

        private final AccountRepository accountRepository;
        private final OperationRepository operationRepository;
        private final NotificationService notificationService;
        private final RibGeneratorService ribGeneratorService;
        private final AccountEventProducer accountEventProducer;

        public AccountEventHandlerService(
                        AccountRepository accountRepository,
                        OperationRepository operationRepository,
                        NotificationService notificationService,
                        RibGeneratorService ribGeneratorService,
                        AccountEventProducer accountEventProducer) {
                this.accountRepository = accountRepository;
                this.operationRepository = operationRepository;
                this.notificationService = notificationService;
                this.ribGeneratorService = ribGeneratorService;
                this.accountEventProducer = accountEventProducer;
        }

        /**
         * Events Handlers
         */
        @EventHandler
        public void handleAccountCreationEvent(@NotNull AccountCreationEvent event) {
                try {
                        log.info("========== HANDLING AccountCreationEvent for account ID: {} ==========",
                                        event.getId());

                        Account account = buildNewAccount(event);
                        account.setAccountNumber(ribGeneratorService.generateAccountNumber());
                        account.setRibKey(ribGeneratorService.calculateRibKey(account.getAccountNumber()));
                        account.setRib(ribGeneratorService.getFormattedRib(
                                        account.getAccountNumber(),
                                        account.getRibKey()));
                        account.setIban(ribGeneratorService.calculateIban(
                                        account.getAccountNumber(),
                                        account.getRibKey()));

                        Account savedAccount = accountRepository.save(account);
                        log.info("Account saved to database: ID={}, AccountNumber={}", savedAccount.getId(),
                                        savedAccount.getAccountNumber());

                        notificationService.sendAccountCreationNotification(
                                        account.getAccountNumber(),
                                        account.getEmail(),
                                        account.getCreatedAt());

                        // Publish to Kafka
                        accountEventProducer.publishAccountCreated(AccountEventDTO.builder()
                                        .accountId(savedAccount.getId())
                                        .customerId(savedAccount.getCustomerId())
                                        .email(savedAccount.getEmail())
                                        .accountNumber(savedAccount.getAccountNumber())
                                        .iban(savedAccount.getIban())
                                        .balance(savedAccount.getBalance())
                                        .status(savedAccount.getStatus())
                                        .timestamp(savedAccount.getCreatedAt())
                                        .eventType("ACCOUNT_CREATED")
                                        .build());

                        log.info("========== COMPLETED AccountCreationEvent handler ==========");

                } catch (Exception e) {
                        log.error("ERROR handling AccountCreationEvent for account {}: {}", event.getId(),
                                        e.getMessage(), e);
                        throw e;
                }
        }

        @EventHandler
        public void handleAccountActivationEvent(@NotNull AccountActivatedEvent event) {
                try {
                        log.info("========== HANDLING AccountActivatedEvent for account ID: {} ==========",
                                        event.getId());

                        Account account = accountRepository
                                        .findById(event.getId())
                                        .orElseThrow(
                                                        () -> new AccountNotFoundException(
                                                                        "Account not found with ID: " + event.getId()));
                        updateAccountStatus(account, event.getAccountStatus(), event.getEventTimestamp());
                        Account activatedAccount = accountRepository.save(account);

                        log.info("Account activated: ID={}, Status={}", activatedAccount.getId(),
                                        activatedAccount.getStatus());

                        notificationService.sendAccountActivationNotification(
                                        account.getAccountNumber(),
                                        account.getEmail(),
                                        event.getEventTimestamp());

                        // Publish to Kafka
                        accountEventProducer.publishAccountActivated(AccountEventDTO.builder()
                                        .accountId(activatedAccount.getId())
                                        .customerId(activatedAccount.getCustomerId())
                                        .email(activatedAccount.getEmail())
                                        .accountNumber(activatedAccount.getAccountNumber())
                                        .status(activatedAccount.getStatus())
                                        .timestamp(event.getEventTimestamp())
                                        .eventType("ACCOUNT_ACTIVATED")
                                        .build());

                        log.info("========== COMPLETED AccountActivatedEvent handler ==========");

                } catch (Exception e) {
                        log.error("ERROR handling AccountActivatedEvent for account {}: {}", event.getId(),
                                        e.getMessage(), e);
                        throw e;
                }
        }

        @EventHandler
        public void handleAccountSuspensionEvent(@NotNull AccountSuspendedEvent event) {
                try {
                        log.info("========== HANDLING AccountSuspendedEvent for account ID: {} ==========",
                                        event.getId());

                        Account account = accountRepository
                                        .findById(event.getId())
                                        .orElseThrow(
                                                        () -> new AccountNotFoundException("Account not found"));
                        updateAccountStatus(account, event.getAccountStatus(), event.getEventTimestamp());
                        Account suspendedAccount = accountRepository.save(account);
                        notificationService.sendAccountSuspensionNotification(
                                        account.getAccountNumber(),
                                        account.getEmail(),
                                        event.getEventTimestamp());

                        // Publish to Kafka
                        accountEventProducer.publishAccountSuspended(AccountEventDTO.builder()
                                        .accountId(suspendedAccount.getId())
                                        .customerId(suspendedAccount.getCustomerId())
                                        .email(suspendedAccount.getEmail())
                                        .accountNumber(suspendedAccount.getAccountNumber())
                                        .status(suspendedAccount.getStatus())
                                        .timestamp(event.getEventTimestamp())
                                        .eventType("ACCOUNT_SUSPENDED")
                                        .build());

                        log.info("========== COMPLETED AccountSuspendedEvent handler ==========");

                } catch (Exception e) {
                        log.error("ERROR handling AccountSuspendedEvent: {}", e.getMessage(), e);
                        throw e;
                }
        }

        @EventHandler
        public void handleAccountDeletedEvent(@NotNull AccountDeletedEvent event) {
                try {
                        log.info("========== HANDLING AccountDeletedEvent for account ID: {} ==========",
                                        event.getId());

                        Account account = accountRepository
                                        .findById(event.getId())
                                        .orElseThrow(
                                                        () -> new AccountNotFoundException("Account not found"));
                        List<Operation> operations = operationRepository.findAllByAccountId(account.getId());

                        operations.forEach(operation -> operation.setDeletedAt(event.getEventTimestamp()));
                        operationRepository.saveAll(operations);

                        account.setDeletedAt(event.getEventTimestamp());
                        accountRepository.save(account);

                        notificationService.sendAccountDeletedNotification(
                                        account.getAccountNumber(),
                                        account.getEmail(),
                                        event.getEventTimestamp());

                        // Publish to Kafka
                        accountEventProducer.publishAccountDeleted(AccountEventDTO.builder()
                                        .accountId(account.getId())
                                        .accountNumber(account.getAccountNumber())
                                        .timestamp(event.getEventTimestamp())
                                        .eventType("ACCOUNT_DELETED")
                                        .build());

                        log.info("========== COMPLETED AccountDeletedEvent handler ==========");
                } catch (Exception e) {
                        log.error("ERROR handling AccountDeletedEvent: {}", e.getMessage(), e);
                        throw e;
                }
        }

        @EventHandler
        public void handleAccountCreditedEvent(@NotNull AccountCreditedEvent event) {
                try {
                        log.info("========== HANDLING AccountCreditedEvent for account ID: {} ==========",
                                        event.getId());

                        Account account = accountRepository
                                        .findById(event.getId())
                                        .orElseThrow(
                                                        () -> new AccountNotFoundException("Account not found"));
                        updateAccountBalance(
                                        account,
                                        event.getAmount(),
                                        event.getEventTimestamp(),
                                        true);
                        Account creditedAccount = accountRepository.save(account);
                        Operation operation = createOperation(
                                        creditedAccount,
                                        OperationType.CREDIT,
                                        event.getAmount(),
                                        event.getDescription(),
                                        event.getEventTimestamp());
                        operationRepository.save(operation);
                        notificationService.sendAccountCreditedNotification(
                                        creditedAccount.getAccountNumber(),
                                        creditedAccount.getEmail(),
                                        event.getAmount(),
                                        event.getEventTimestamp());

                        // Publish to Kafka
                        accountEventProducer.publishAccountCredited(AccountEventDTO.builder()
                                        .accountId(creditedAccount.getId())
                                        .customerId(creditedAccount.getCustomerId())
                                        .accountNumber(creditedAccount.getAccountNumber())
                                        .balance(creditedAccount.getBalance())
                                        .amount(event.getAmount())
                                        .description(event.getDescription())
                                        .timestamp(event.getEventTimestamp())
                                        .eventType("ACCOUNT_CREDITED")
                                        .build());

                        log.info("========== COMPLETED AccountCreditedEvent handler ==========");
                } catch (Exception e) {
                        log.error("ERROR handling AccountCreditedEvent: {}", e.getMessage(), e);
                        throw e;
                }
        }

        @EventHandler
        public void handleAccountDebitedEvent(@NotNull AccountDebitedEvent event) {
                try {
                        log.info("========== HANDLING AccountDebitedEvent for account ID: {} ==========",
                                        event.getId());

                        Account account = accountRepository
                                        .findById(event.getId())
                                        .orElseThrow(
                                                        () -> new AccountNotFoundException("Account not found"));

                        updateAccountBalance(
                                        account,
                                        event.getAmount(),
                                        event.getEventTimestamp(),
                                        false);

                        Account debitedAccount = accountRepository.save(account);
                        Operation operation = createOperation(
                                        debitedAccount,
                                        OperationType.DEBIT,
                                        event.getAmount(),
                                        event.getDescription(),
                                        event.getEventTimestamp());
                        operationRepository.save(operation);
                        notificationService.sendAccountDebitedNotification(
                                        debitedAccount.getAccountNumber(),
                                        debitedAccount.getEmail(),
                                        event.getAmount(),
                                        event.getEventTimestamp());

                        // Publish to Kafka
                        accountEventProducer.publishAccountDebited(AccountEventDTO.builder()
                                        .accountId(debitedAccount.getId())
                                        .customerId(debitedAccount.getCustomerId())
                                        .accountNumber(debitedAccount.getAccountNumber())
                                        .balance(debitedAccount.getBalance())
                                        .amount(event.getAmount())
                                        .description(event.getDescription())
                                        .timestamp(event.getEventTimestamp())
                                        .eventType("ACCOUNT_DEBITED")
                                        .build());

                        log.info("========== COMPLETED AccountDebitedEvent handler ==========");
                } catch (Exception e) {
                        log.error("ERROR handling AccountDebitedEvent: {}", e.getMessage(), e);
                        throw e;
                }
        }

        /**
         * Helpers
         */
        // Build a new Account entity from AccountCreationEvent
        private Account buildNewAccount(@NotNull AccountCreationEvent event) {
                return Account
                                .builder()
                                .id(event.getId())
                                .email(event.getEmail())
                                .customerId(event.getCustomerId())
                                .status(event.getAccountStatus())
                                .balance(event.getBalance())
                                .createdAt(event.getEventTimestamp())
                                .build();
        }

        // Update account status and updatedAt timestamp
        private void updateAccountStatus(
                        @NotNull Account account,
                        AccountStatus status,
                        LocalDateTime eventTimestamp) {
                account.setStatus(status);
                account.setUpdatedAt(eventTimestamp);
        }

        // Update account balance and updatedAt timestamp
        private void updateAccountBalance(
                        @NotNull Account account,
                        BigDecimal amount,
                        LocalDateTime eventTimestamp,
                        boolean isCredit) {
                account.setUpdatedAt(eventTimestamp);
                if (isCredit) {
                        account.setBalance(account.getBalance().add(amount));
                } else {
                        account.setBalance(account.getBalance().subtract(amount));
                }
        }

        // Create Operation entity
        private Operation createOperation(
                        Account account,
                        OperationType type,
                        BigDecimal amount,
                        String description,
                        LocalDateTime eventTimestamp) {
                return Operation
                                .builder()
                                .account(account)
                                .type(type)
                                .amount(amount)
                                .description(description)
                                .createdAt(eventTimestamp)
                                .build();
        }
}
