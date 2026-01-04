package com.exemple.authservice.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;


@Builder
public record RegisterRequest(
        // ========== Champs obligatoires ==========

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email ,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password ,

        @NotBlank(message = "First name is required")
        String firstName ,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "CIN or Passport is required")
        @Pattern(regexp = "^[A-Z0-9]{5,20}$", message = "CIN or Passport must be alphanumeric and between 5 to 20 characters")
        String cinOrPassport,

        // ========== Champs optionnels ==========

        @NotBlank(message = "Nationality is required")
        @Size(max = 50, message = "Nationality must be at most 50 characters")
        String nationality,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotBlank(message = "gender is required")
        @Size(max = 1, message = "gender must be at most 1 character")
        String gender,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
        String phone,

        @Size(max = 100, message = "Address must be at most 100 characters")
        String address,

        @Size(max = 50, message = "City must be at most 50 characters")
        String city,

        @Size(max = 50, message = "Country must be at most 50 characters")
        String country,

        @Size(max = 100, message = "Profession must be at most 100 characters")
        String profession
) {
}
