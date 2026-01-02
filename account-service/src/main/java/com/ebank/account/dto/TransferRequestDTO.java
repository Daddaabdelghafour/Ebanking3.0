package com.ebank.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDTO(
        @NotBlank(message = "Source account must not be blank")
        UUID sourceAccount,

        @NotBlank(message = "Destination account must not be blank")
        UUID destinationAccount,

        @NotNull(message = "Amount must not be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Description must not be blank")
        String description
) {
}
