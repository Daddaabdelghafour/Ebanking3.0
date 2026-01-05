package com.ebank.notification.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record EmailVerifiedEvent(
    String eventId,
    boolean verified,
    String email,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp
) {}