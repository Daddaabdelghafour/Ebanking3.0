package com.exemple.authservice.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record NewRegistredEvent(
        String eventId,
        String email,
        String firstName,
        String lastName,
        String verificationCode,
        String validityPeriod, // e.g., "15 minutes"

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
}
