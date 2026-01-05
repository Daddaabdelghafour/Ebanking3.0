package com.ebank.account.Queries.exception;

public class OperationNotFoundException extends RuntimeException {
    public OperationNotFoundException(String message) {
        super(message);
    }
}
