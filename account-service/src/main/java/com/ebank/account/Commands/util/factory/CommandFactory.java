package com.ebank.account.Commands.util.factory;

import com.ebank.account.Commands.command.*;
import com.ebank.account.Common.enums.AccountStatus;
import com.ebank.account.Commands.dto.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Default implementation of CommandFactory.
 * Creates command objects with current timestamp and provided parameters.
 */
public class CommandFactory {
    @NotNull
    @Contract("_ -> new")
    public static CreditAccountCommand createCreditAccountCommand(@NotNull final OperationRequestDTO dto) {
        return new CreditAccountCommand(
                dto.accountId(),
                LocalDateTime.now(),
                dto.amount(),
                dto.description()
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static DebitAccountCommand createDebitAccountCommand(@NotNull final OperationRequestDTO dto) {
        return new DebitAccountCommand(
                dto.accountId(),
                LocalDateTime.now(),
                dto.amount(),
                dto.description()
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static ActivateAccountCommand createActivateAccountCommand(@NotNull final UpdateAccountStatusRequestDTO dto) {
        return new ActivateAccountCommand(
                dto.accountId(),
                LocalDateTime.now(),
                AccountStatus.ACTIVATED
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static SuspendAccountCommand createSuspendAccountCommand(@NotNull final UpdateAccountStatusRequestDTO dto) {
        return new SuspendAccountCommand(
                dto.accountId(),
                LocalDateTime.now(),
                AccountStatus.SUSPENDED
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static DeleteAccountCommand createDeleteAccountCommand(@NotNull final UUID accountId) {
        return new DeleteAccountCommand(
                accountId,
                LocalDateTime.now()
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static DebitAccountCommand createDebitAccountCommand(@NotNull final TransferRequestDTO dto) {
        return new DebitAccountCommand(
                dto.sourceAccount(),
                LocalDateTime.now(),
                dto.amount(),
                dto.description()
        );
    }


    @NotNull
    @Contract("_ -> new")
    public static CreditAccountCommand createCreditAccountCommand(@NotNull final TransferRequestDTO dto) {
        return new CreditAccountCommand(
                dto.destinationAccount(),
                LocalDateTime.now(),
                dto.amount(),
                dto.description()
        );
    }

    @NotNull
    @Contract("_ -> new")
    public static CreditAccountCommand createCreditAccountCommandReverse(@NotNull final TransferRequestDTO dto) {
        return new CreditAccountCommand(
                dto.sourceAccount(),
                LocalDateTime.now(),
                dto.amount(),
                dto.description()
        );
    }

    @NotNull
    @Contract("_ -> new")
    public static CreateAccountCommand createAccountCommand(@NotNull final AccountRequestDTO dto) {
        return new CreateAccountCommand(
                UUID.randomUUID(),
                LocalDateTime.now(),
                dto.customerId(),
                dto.email(),
                dto.balance(),
                AccountStatus.CREATED
        );
    }

    @NotNull
    @Contract("_,_ -> new")
    public static CreditAccountCommand creditAccountCommand(UUID accountId, @NotNull final OperationRequestDTO dto) {
        return new CreditAccountCommand(
                accountId,
                LocalDateTime.now(),
                dto.amount(),
                dto.description()
        );
    }

    @NotNull
    @Contract("_,_ -> new")
    public static DebitAccountCommand debitAccountCommand(UUID accountId, @NotNull final OperationRequestDTO dto) {
        return new DebitAccountCommand(
                accountId,
                LocalDateTime.now(),
                dto.amount(),
                dto.description()
        );
    }

    @NotNull
    @Contract("_ -> new")
    public static ActivateAccountCommand activateAccountCommand(UUID accountId) {
        return new ActivateAccountCommand(
                accountId,
                LocalDateTime.now(),
                AccountStatus.ACTIVATED
        );
    }

    @NotNull
    @Contract("_ -> new")
    public static SuspendAccountCommand suspendAccountCommand(UUID accountId) {
        return new SuspendAccountCommand(
                accountId,
                LocalDateTime.now(),
                AccountStatus.SUSPENDED
        );
    }

    @NotNull
    @Contract("_ -> new")
    public static DeleteAccountCommand deleteAccountCommand(UUID accountId) {
        return new DeleteAccountCommand(
                accountId,
                LocalDateTime.now()
        );
    }
}

