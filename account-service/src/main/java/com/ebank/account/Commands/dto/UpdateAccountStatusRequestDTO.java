package com.ebank.account.Commands.dto;

import com.ebank.account.Common.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateAccountStatusRequestDTO(
        @NotNull(message = "Account ID cannot be null")
        UUID accountId,
        @NotNull(message = "Account status cannot be null")
        AccountStatus accountStatus
) {
}
