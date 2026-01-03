// AddBeneficiaryRequestDTO.java
package com.ebank.account.Commands.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AddBeneficiaryRequestDTO(
        @NotNull(message = "Account ID must not be null")
        UUID accountId,

        @NotBlank(message = "Beneficiary name must not be blank")
        @Size(min = 2, max = 100, message = "Beneficiary name must be between 2 and 100 characters")
        String beneficiaryName,

        @NotBlank(message = "Beneficiary RIB must not be blank")
        @Size(min = 24, max = 28, message = "Beneficiary RIB must be between 24 and 28 characters")
        @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$", message = "Invalid IBAN format")
        String beneficiaryRib
) {
}