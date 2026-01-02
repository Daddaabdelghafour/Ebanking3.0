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
public class CustomerEventDTO {
    private UUID customerId;
    private String email;
    private String firstName;
    private String lastName;
    private BigDecimal initialBalance;
    private LocalDateTime timestamp;
    private String eventType;
}
