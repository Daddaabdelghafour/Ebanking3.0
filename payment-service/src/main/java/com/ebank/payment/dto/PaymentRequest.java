package com.ebank.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequest {
    @NotNull(message = "Source account ID is required")
    private UUID sourceAccountId;

    @NotNull(message = "Target account ID is required")
    private UUID targetAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    private String currency = "USD";

    private String description;
}

