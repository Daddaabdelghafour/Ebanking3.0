package org.example.kycservice.customerService.dto;

import java.util.UUID;

public record CustomerCreatedEvent(
        UUID userId
) {

}
