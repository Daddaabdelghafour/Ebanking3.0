package com.ebank.account.Commands.aggregate;

import com.ebank.account.Commands.command.*;
import com.ebank.account.Commands.exception.*;
import com.ebank.account.Commands.util.factory.EventFactory;
import com.ebank.account.Common.enums.AccountStatus;
import com.ebank.account.Common.event.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Aggregate
@Getter
@Slf4j
public class AccountAggregate {

    @AggregateIdentifier
    private UUID accountId;
    private UUID customerId;
    private String email;
    private BigDecimal balance;
    private String accountNumber;
    private String ribKey;
    private String iban;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public AccountAggregate() {
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command){
        AccountCreationEvent event = EventFactory.create(command);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(@NotNull AccountCreationEvent event){
        this.accountId = event.getId();
        this.customerId = event.getCustomerId();
        this.email = event.getEmail();
        this.balance = event.getBalance();
        this.accountStatus = event.getAccountStatus();
        this.createdAt = event.getEventTimestamp();
        AccountActivatedEvent accountActivatedEvent = EventFactory.create(this.accountId, this.createdAt );
        AggregateLifecycle.apply(accountActivatedEvent);
    }

    @CommandHandler
    public void handle(ActivateAccountCommand command){
        AccountActivatedEvent event = EventFactory.create(command);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(@NotNull AccountActivatedEvent event){
        this.accountId = event.getId();
        this.updatedAt = event.getEventTimestamp();
        this.accountStatus = event.getAccountStatus();
    }

    @CommandHandler
    public void handle(SuspendAccountCommand command){
        AccountSuspendedEvent event = EventFactory.create(command);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(@NotNull CreditAccountCommand command) {
        if(command.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new AmountNotSufficientException(" amount not sufficient => " + command.getAmount());
        }else if (!this.accountStatus.equals(AccountStatus.ACTIVATED)) {
            throw new AccountNotActivatedException("Account not activated => " + this.accountStatus);
        }else{
            AccountCreditedEvent event = EventFactory.create(command);
            AggregateLifecycle.apply(event);
        }
    }

    @EventSourcingHandler
    public void on(@NotNull AccountCreditedEvent event) {
        this.accountId = event.getId();
        this.updatedAt = event.getEventTimestamp();
        this.balance = this.balance.add(event.getAmount());
    }

    @CommandHandler
    public void handle(@NotNull DebitAccountCommand command) {
        if (this.balance.compareTo(BigDecimal.ZERO) < 0 || this.balance.compareTo(command.getAmount()) < 0) {
            throw new BalanceNotSufficientException("Balance not sufficient => " + this.balance);
        } else if (!this.accountStatus.equals(AccountStatus.ACTIVATED)) {
            throw new AccountNotActivatedException("Account not activated => " + this.accountStatus);
        } else {
            AccountDebitedEvent event = EventFactory.create(command);
            AggregateLifecycle.apply(event);
        }
    }

    @EventSourcingHandler
    public void on(@NotNull AccountDebitedEvent event) {
        this.accountId = event.getId();
        this.balance = this.balance.subtract(event.getAmount());
        this.updatedAt = event.getEventTimestamp();
    }

    @CommandHandler
    public void handle(@NotNull DeleteAccountCommand command) {
        if (this.balance.compareTo(BigDecimal.ZERO) > 0) {
            throw new AccountDeletionNotAllowedException(
                "Cannot delete account with non-zero balance: " + this.balance);
        }
        AccountDeletedEvent event = EventFactory.create(command);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(@NotNull AccountDeletedEvent event) {
        this.accountId = event.getId();
        this.deletedAt = event.getEventTimestamp();
        this.email = null;
        this.customerId = null;
    }
}

