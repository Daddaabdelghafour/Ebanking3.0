package com.exemple.authservice.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UserRegisteredEvent(
        String eventId,
        String keycloakUserId,
        String email,
        String firstName,
        String lastName,
        String phone,
        String address,
        String city,
        String country,
        String profession,
        String gender,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth, // ISO 8601 format (yyyy-MM-dd)

        String nationality,
        String cinOrPassport, // CIN or Passport number

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
         LocalDateTime timestamp
) {
}
