package com.ebank.notification.dto;

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
public class NotificationResponse {
    private UUID id;
    private UUID userId;
    private String type;
    private String channel;
    private String message;
    private String status; // PENDING, SENT, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}

