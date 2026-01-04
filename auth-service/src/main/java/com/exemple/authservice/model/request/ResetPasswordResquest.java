package com.exemple.authservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordResquest(

        @NotBlank(message = "Email is required")
        @Email
        String email,

        @NotBlank(message = "Reset code is required")
        @Size(min = 6, max = 6)
        String code,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword
) {
}
