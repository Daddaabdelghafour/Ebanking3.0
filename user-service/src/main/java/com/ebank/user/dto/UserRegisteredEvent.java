package com.ebank.user.dto;

import com.ebank.user.Enum.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.LocalDate;

public record UserRegisteredEvent(
         String cinOrPassport,
         String keycloakUserId,
         String nationality,
         String firstName,
         String lastName,
         Gender gender,
         String email,
         String phone,
         String address,
         String city,
         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
         @JsonDeserialize(using = LocalDateDeserializer.class)
         LocalDate dateOfBirth,         String country
) {
}
