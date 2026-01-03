package com.ebank.account.Queries.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(UUID accountId) {
        super("Account with ID " + accountId + " not found");
    }
}
