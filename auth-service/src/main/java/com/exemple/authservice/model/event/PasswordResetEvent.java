package com.exemple.authservice.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PasswordResetEvent(
         String eventId,
         String eventType, // "PASSWORD_RESET_REQUESTED"
         String keycloakUserId,
         String email,
         String resetCode, // Seulement pour REQUESTED

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
         LocalDateTime timestamp
) {
}
