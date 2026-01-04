package com.exemple.authservice.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EmailVerificationEvent(
         String eventId,
         boolean verified, // true if email verified, false otherwise
         String email,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
         LocalDateTime timestamp
) {
}
