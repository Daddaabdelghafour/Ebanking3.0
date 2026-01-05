package com.ebank.account.Queries.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NotificationRequestDTO(
        @Email(message = "Email is not valid")
        @NotBlank(message = "Email is required")
        String to,

        @NotBlank(message = "Subject is required!")
        String subject,

        @NotBlank(message = "Message body is required!")
        String body
) {
}
