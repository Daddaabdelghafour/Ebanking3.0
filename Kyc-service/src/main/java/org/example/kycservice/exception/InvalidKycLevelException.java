package org.example.kycservice.exception;

public class InvalidKycLevelException extends RuntimeException {
    public InvalidKycLevelException(String message) {
        super(message);
    }
}
