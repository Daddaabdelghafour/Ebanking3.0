// TransactionResponseDTO.java
package com.ebank.account.Queries.dto;

import com.ebank.account.Common.enums.TransactionStatus;
import com.ebank.account.Common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDTO(
        UUID id,
        UUID sourceAccountId,
        UUID destinationAccountId,
        String sourceAccountNumber,
        String destinationAccountNumber,
        BigDecimal amount,
        TransactionStatus status,
        TransactionType type,
        String reference,
        String failureReason,
        LocalDateTime createdAt
) {
}