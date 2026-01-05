package com.ebank.user.dto;

import java.util.UUID;

public record NotificationEvent(
        UUID userId,
        String firstName,
        String lastName,
        String email
) {
}
