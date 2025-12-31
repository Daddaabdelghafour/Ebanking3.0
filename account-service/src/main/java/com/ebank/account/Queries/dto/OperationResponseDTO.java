package com.ebank.account.Queries.dto;

import com.ebank.account.Common.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OperationResponseDTO(
        UUID id,
        OperationType type,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {
}
