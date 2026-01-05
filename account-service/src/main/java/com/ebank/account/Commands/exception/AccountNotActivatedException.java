package com.ebank.account.Commands.exception;

import java.util.UUID;

public class AccountNotActivatedException extends RuntimeException {
    public AccountNotActivatedException(String message) {
        super(message);
    }

    public AccountNotActivatedException(UUID accountId) {
        super("Account with ID " + accountId + " is not activated");
    }
}
