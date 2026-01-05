package com.exemple.authservice.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResendVerificationCodeEvent(
        String eventId,
        String email,
        String verificationCode,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
}
