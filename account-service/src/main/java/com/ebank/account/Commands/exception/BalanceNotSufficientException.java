package com.ebank.account.Commands.exception;

public class BalanceNotSufficientException extends RuntimeException {
    public BalanceNotSufficientException(String message) {
        super(message);
    }
}
