package com.ebank.user.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.customerservice.Enum.Gender;

import java.time.LocalDate;

public record CustomerDto(
         String cinOrPassport,
         String nationality,
         String firstName,
         String lastName,
         Gender gender,
         String email,
         String phone,
         String address,
         String city,
         @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
         String country
) {
}
