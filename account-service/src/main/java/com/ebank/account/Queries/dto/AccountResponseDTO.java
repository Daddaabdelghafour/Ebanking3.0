package com.ebank.account.Queries.dto;

import com.ebank.account.Common.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponseDTO(
        UUID id,
        UUID customerId,
        BigDecimal balance,
        String accountNumber,
        String ribKey,
        String iban,
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
        ) {
}
