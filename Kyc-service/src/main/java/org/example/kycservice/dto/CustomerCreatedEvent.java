package org.example.kycservice.dto;

import java.util.UUID;


public record CustomerCreatedEvent(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String address,
        String city,
        String country,
        String role
) {}
