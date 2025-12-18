package com.ebank.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class NotificationRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Message is required")
    private String message;

    private String type; // EMAIL, SMS, PUSH, ALERT
    private String channel; // EMAIL, SMS, PUSH
}

