package com.ebank.account.Commands.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record OperationRequestDTO(
        @NotNull(message = "Account ID cannot be blank")
        UUID accountId,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Description cannot be blank")
        String description
) {
}
