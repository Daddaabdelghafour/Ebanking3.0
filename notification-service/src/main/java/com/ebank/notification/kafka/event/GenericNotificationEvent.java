package com.ebank.notification.kafka.event;

import java.time.LocalDateTime;
import java.util.Map;

public record GenericNotificationEvent(
    String notificationId,
    String recipient,
    String channel,
    String subject,
    String message,
    Map<String, Object> metadata,
    LocalDateTime timestamp
) {}