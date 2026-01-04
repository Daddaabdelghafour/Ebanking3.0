package com.ebank.notification.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ResendVerificationCodeEvent(
    String eventId,
    String email,
    String verificationCode,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp
) {}