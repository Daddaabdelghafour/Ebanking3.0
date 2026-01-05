package com.ebank.account.Commands.util.factory;

import com.ebank.account.Commands.command.*;
import com.ebank.account.Common.event.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Default implementation of EventFactory.
 * Transforms command objects into their corresponding domain events.
 */
public class EventFactory {


    @NotNull
    @Contract("_ -> new")
    public static AccountCreationEvent create(@NotNull final CreateAccountCommand command) {
        return new AccountCreationEvent(
                command.getId(),
                command.getCommandTimestamp(),
                command.getCustomerId(),
                command.getEmail(),
                command.getBalance()
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static AccountActivatedEvent create(@NotNull final ActivateAccountCommand command) {
        return new AccountActivatedEvent(
                command.getId(),
                command.getCommandTimestamp()
        );
    }


    @NotNull
    @Contract("_,_ -> new")
    public static AccountActivatedEvent create(final UUID accountId, final LocalDateTime commandTimestamp) {
        return new AccountActivatedEvent(
                accountId,
                commandTimestamp
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static AccountSuspendedEvent create(@NotNull final SuspendAccountCommand command) {
        return new AccountSuspendedEvent(
                command.getId(),
                command.getCommandTimestamp()
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static AccountCreditedEvent create(@NotNull final CreditAccountCommand command) {
        return new AccountCreditedEvent(
                command.getId(),
                command.getCommandTimestamp(),
                command.getAmount(),
                command.getDescription()
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static AccountDebitedEvent create(@NotNull final DebitAccountCommand command) {
        return new AccountDebitedEvent(
                command.getId(),
                command.getCommandTimestamp(),
                command.getAmount(),
                command.getDescription()
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static AccountDeletedEvent create(@NotNull final DeleteAccountCommand command) {
        return new AccountDeletedEvent(
                command.getId(),
                command.getCommandTimestamp()
        );
    }
}