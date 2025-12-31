package com.ebank.account.Queries.service;

import com.ebank.account.Common.enums.AccountStatus;
import com.ebank.account.Common.enums.OperationType;
import com.ebank.account.Common.event.*;
import com.ebank.account.Common.service.RibGeneratorService;
import com.ebank.account.Queries.entity.Account;
import com.ebank.account.Queries.entity.Operation;
import com.ebank.account.Queries.exception.AccountNotFoundException;
import com.ebank.account.Queries.repository.AccountRepository;
import com.ebank.account.Queries.repository.OperationRepository;
import com.ebank.account.Queries.service.notification.NotificationService;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountEventHandlerService {

    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final NotificationService notificationService;
    private final RibGeneratorService ribGeneratorService;

    public AccountEventHandlerService(
            AccountRepository accountRepository,
            OperationRepository operationRepository,
            NotificationService notificationService,
            RibGeneratorService ribGeneratorService
    ) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.notificationService = notificationService;
        this.ribGeneratorService = ribGeneratorService;
    }

    /**
     * Events Handlers
     */
    @EventHandler
    public Account handleAccountCreationEvent(
            @NotNull AccountCreationEvent event
    ){
          Account account = buildNewAccount(event);
          account.setAccountNumber(ribGeneratorService.generateAccountNumber());
          account.setRibKey(ribGeneratorService.calculateRibKey(account.getAccountNumber()));
          account.setIban(ribGeneratorService.calculateIban(
                  account.getAccountNumber(),
                  account.getRibKey()
          ));

          Account savedAccount = accountRepository.save(account);
          notificationService.sendAccountCreationNotification(
                 account.getAccountNumber(),
                 account.getEmail(),
                 account.getCreatedAt()
          );
          return savedAccount;
    }

    @EventHandler
    public Account handleAccountActivationEvent(
            @NotNull AccountActivatedEvent event
    ){
        Account account = accountRepository
                .findById(event.getId())
                .orElseThrow(
                        () -> new AccountNotFoundException("Account not found")
                );
        updateAccountStatus(account, event.getAccountStatus(), event.getEventTimestamp());
        Account activatedAccount = accountRepository.save(account);
        notificationService.sendAccountActivationNotification(
                account.getAccountNumber(),
                account.getEmail(),
                event.getEventTimestamp()
        );
        return activatedAccount;

    }

    @EventHandler
    public Account handleAccountSuspensionEvent(
            @NotNull AccountSuspendedEvent event
    ){

        Account account = accountRepository
                .findById(event.getId())
                .orElseThrow(
                        () -> new AccountNotFoundException("Account not found")
                );
        updateAccountStatus(account, event.getAccountStatus(), event.getEventTimestamp());
        Account suspendedAccount = accountRepository.save(account);
        notificationService.sendAccountSuspensionNotification(
                account.getAccountNumber(),
                account.getEmail(),
                event.getEventTimestamp()
        );
        return suspendedAccount;
    }

    @EventHandler
    public void handleAccountDeletedEvent(
            @NotNull AccountDeletedEvent event
    ){
        Account account = accountRepository
                .findById(event.getId())
                .orElseThrow(
                        () -> new AccountNotFoundException("Account not found")
                );
        List<Operation> operations = operationRepository.findAllByAccountId(account.getId());

        operations.forEach(operation -> operation.setDeletedAt(event.getEventTimestamp()));
        operationRepository.saveAll(operations);

        account.setDeletedAt(event.getEventTimestamp());
        accountRepository.save(account);

        notificationService.sendAccountDeletedNotification(
                account.getAccountNumber(),
                account.getEmail(),
                event.getEventTimestamp()
        );
    }

    @EventHandler
    public Operation handleAccountCreditedEvent(
            @NotNull AccountCreditedEvent event
    ){
        Account account = accountRepository
                .findById(event.getId())
                .orElseThrow(
                        () -> new AccountNotFoundException("Account not found")
                );
        updateAccountBalance(
                account,
                event.getAmount(),
                event.getEventTimestamp(),
                true
        );
        Account creditedAccount = accountRepository.save(account);
        Operation operation = createOperation(
                creditedAccount,
                OperationType.CREDIT,
                event.getAmount(),
                event.getDescription(),
                event.getEventTimestamp()
        );
        Operation creditOperation = operationRepository.save(operation);
        notificationService.sendAccountCreditedNotification(
                creditedAccount.getAccountNumber(),
                creditedAccount.getEmail(),
                event.getAmount(),
                event.getEventTimestamp()
        );
        return creditOperation;
    }

    @EventHandler
    public Operation handleAccountDebitedEvent(
            @NotNull AccountDebitedEvent event
    ){
        Account account = accountRepository
                .findById(event.getId())
                .orElseThrow(
                        () -> new AccountNotFoundException("Account not found")
                );

        updateAccountBalance(
                account,
                event.getAmount(),
                event.getEventTimestamp(),
                false
        );

        Account debitedAccount = accountRepository.save(account);
        Operation operation = createOperation(
                debitedAccount,
                OperationType.DEBIT,
                event.getAmount(),
                event.getDescription(),
                event.getEventTimestamp()
        );
        Operation debitOperation = operationRepository.save(operation);
        notificationService.sendAccountDebitedNotification(
                debitedAccount.getAccountNumber(),
                debitedAccount.getEmail(),
                event.getAmount(),
                event.getEventTimestamp()
        );
        return debitOperation;
    }


    /**
     * Helpers
     */
    // Build a new Account entity from AccountCreationEvent
    private Account buildNewAccount(
            @NotNull AccountCreationEvent event
    ){
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
            LocalDateTime eventTimestamp
    ){
        account.setStatus(status);
        account.setUpdatedAt(eventTimestamp);
    }

    // Update account balance and updatedAt timestamp
    private void updateAccountBalance(
            @NotNull Account account,
            BigDecimal amount,
            LocalDateTime eventTimestamp,
            boolean isCredit
    ){
        account.setUpdatedAt(eventTimestamp);
        if(isCredit){
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
            LocalDateTime eventTimestamp
    ){
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