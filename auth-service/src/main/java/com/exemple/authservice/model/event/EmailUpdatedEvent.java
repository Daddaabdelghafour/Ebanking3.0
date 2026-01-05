package com.exemple.authservice.model.event;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EmailUpdatedEvent(
        String eventId,
        String oldEmail,
        String newEmail,
        @JsonFormat (pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
}
