package com.ebank.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AccountCreateRequest {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    private String accountType; // SAVINGS, CHECKING, etc.
}

