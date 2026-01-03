// ConfirmTransactionRequestDTO.java
package com.ebank.account.Commands.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ConfirmTransactionRequestDTO(
        @NotNull(message = "Transaction ID must not be null")
        UUID transactionId,

        @NotBlank(message = "OTP code must not be blank")
        @Size(min = 6, max = 6, message = "OTP code must be 6 digits")
        @Pattern(regexp = "\\d{6}", message = "OTP code must contain only digits")
        String otpCode
) {
}