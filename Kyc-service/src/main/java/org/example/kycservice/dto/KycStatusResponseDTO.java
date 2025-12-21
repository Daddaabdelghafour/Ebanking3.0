package org.example.kycservice.dto;
import java.util.List;
import java.util.UUID;

public record KycStatusResponseDTO(
        UUID userId,
        String kycLevel,
        String kycStatus,
        List<KycDocumentDTO> documents
) {}
