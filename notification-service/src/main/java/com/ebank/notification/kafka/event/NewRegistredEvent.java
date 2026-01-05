package com.ebank.notification.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record NewRegistredEvent(
    String eventId,
    String email,
    String firstName,
    String lastName,
    String verificationCode,
    String validityPeriod,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp
) {}