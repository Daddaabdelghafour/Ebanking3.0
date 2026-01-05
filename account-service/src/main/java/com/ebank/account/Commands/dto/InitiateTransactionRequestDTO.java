// InitiateTransactionRequestDTO.java
package com.ebank.account.Commands.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record InitiateTransactionRequestDTO(
        @NotNull(message = "Source account must not be null")
        UUID sourceAccountId,

        @NotNull(message = "Beneficiary must not be null")
        UUID beneficiaryId,

        @NotNull(message = "Amount must not be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Reference must not be blank")
        @Size(max = 200, message = "Reference must not exceed 200 characters")
        String reference
) {
}