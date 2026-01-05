package com.ebank.user.dto;

import com.ebank.user.Enum.ROLE;

import java.util.UUID;

public record CustomerCreatedEvent(
    UUID userId,
    String keycloakUserId,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String address,
    String city,
    String country,
    ROLE role
) {}
