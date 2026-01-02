package com.ebank.account.Commands.exception;

public class AmountNotSufficientException extends RuntimeException {
    public AmountNotSufficientException(String message) {
        super(message);
    }
}
