package org.example.kycservice.dto;
import java.util.List;
import java.util.UUID;

public record KycRecordDTO(
        UUID id,
        UUID userId,
        String kycLevel,
        String kycStatus,
        String createdAt,
        String updatedAt,
        List<KycDocumentDTO> documents
) {}
