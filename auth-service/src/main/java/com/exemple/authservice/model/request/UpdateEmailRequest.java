package com.exemple.authservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateEmailRequest(
        @NotBlank(message = "New email is required")
        @Email
        String newEmail,

        @NotBlank(message = "Password is required for email change")
        String password
) {
}
