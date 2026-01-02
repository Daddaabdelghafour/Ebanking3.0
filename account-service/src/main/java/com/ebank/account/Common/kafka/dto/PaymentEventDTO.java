package com.ebank.account.Common.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventDTO {
    private UUID paymentId;
    private UUID accountId;
    private BigDecimal amount;
    private String description;
    private String transactionReference;
    private LocalDateTime timestamp;
    private String eventType;
}
