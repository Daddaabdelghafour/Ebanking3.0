package com.ebank.payment.dto;

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
public class PaymentResponse {
    private UUID id;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private Double amount;
    private String currency;
    private String status; // PENDING, COMPLETED, FAILED, CANCELLED
    private String type;
    private String failureReason;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

