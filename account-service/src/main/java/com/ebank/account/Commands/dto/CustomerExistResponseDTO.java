package com.ebank.account.Commands.dto;

import java.util.UUID;

public record CustomerExistResponseDTO(
        UUID customerId,
        String email
) {
}
