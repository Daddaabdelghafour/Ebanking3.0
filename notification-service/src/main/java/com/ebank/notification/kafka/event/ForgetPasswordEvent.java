package com.ebank.notification.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ForgetPasswordEvent(
    String eventId,
    String email,
    String resetCode,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp
) {}