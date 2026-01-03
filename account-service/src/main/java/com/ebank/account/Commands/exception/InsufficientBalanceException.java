// InsufficientBalanceException.java (if not already exists)
package com.ebank.account.Commands.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(BigDecimal balance, BigDecimal amount) {
        super(String.format("Insufficient balance. Available: %s, Required: %s", balance, amount));
    }
}