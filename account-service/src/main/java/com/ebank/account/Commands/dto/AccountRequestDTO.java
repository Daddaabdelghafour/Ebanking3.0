package com.ebank.account.Commands.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequestDTO(
        @NotNull(message = "Customer ID cannot be blank")
        UUID customerId,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Initial balance cannot be null")
        @PositiveOrZero(message = "Initial balance must be zero or positive")
        BigDecimal balance
) {
}
