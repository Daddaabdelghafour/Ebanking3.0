package com.ebank.account.Queries.exception;

public class AccountCreationFailedException extends RuntimeException {
    public AccountCreationFailedException(String message) {
        super(message);
    }
}
