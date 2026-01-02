package com.ebank.account.dto;

import java.util.UUID;

public record CustomerExistResponseDTO(
        UUID customerId,
        String email
) {
}
