// BeneficiaryResponseDTO.java
package com.ebank.account.Queries.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BeneficiaryResponseDTO(
        UUID id,
        String beneficiaryName,
        String beneficiaryRib,
        Boolean isActive,
        LocalDateTime createdAt
) {
}