package com.ebank.account.Common.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudEventDTO {
    private UUID accountId;
    private String reason;
    private String severity;
    private LocalDateTime timestamp;
    private String eventType;
}
