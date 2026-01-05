package com.ebank.notification.kafka.event;

import java.time.LocalDateTime;

public record AccountCreatedEvent(
    String accountId,
    String customerId,
    String email,
    String accountNumber,
    String iban,
    Double balance,
    String status,
    LocalDateTime timestamp,
    String eventType
) {}