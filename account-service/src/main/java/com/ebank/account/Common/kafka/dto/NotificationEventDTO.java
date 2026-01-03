// NotificationEventDTO.java
package com.ebank.account.Common.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {
    private UUID notificationId;
    private String recipient; // Email address
    private String channel; // TRANSACTION_OTP, TRANSACTION_SUCCESS, TRANSACTION_FAILED, BENEFICIARY_ADDED
    private String subject;
    private String message;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
}