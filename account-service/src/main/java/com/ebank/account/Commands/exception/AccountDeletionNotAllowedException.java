package com.ebank.account.Commands.exception;

public class AccountDeletionNotAllowedException extends RuntimeException {
    public AccountDeletionNotAllowedException(String message) {
        super(message);
    }
}
