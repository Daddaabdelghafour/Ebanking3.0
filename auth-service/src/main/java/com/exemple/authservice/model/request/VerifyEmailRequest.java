package com.exemple.authservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyEmailRequest(

        @NotBlank(message = "Email is required")
        @Email
        String email,

        @NotBlank(message = "Verification code is required")
        @Size(min = 6, max = 6, message = "Code must be 6 digits")
        String verificationCode
) {
}
