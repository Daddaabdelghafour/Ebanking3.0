// InvalidOtpException.java
package com.ebank.account.Commands.exception;

public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException() {
        super("Invalid or expired OTP code");
    }
    
    public InvalidOtpException(String message) {
        super(message);
    }
}