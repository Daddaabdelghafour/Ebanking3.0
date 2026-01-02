package com.ebank.account.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AccountRequestDTO(
        @NotBlank(message = "Customer ID cannot be blank")
        UUID customerId
) {
}
