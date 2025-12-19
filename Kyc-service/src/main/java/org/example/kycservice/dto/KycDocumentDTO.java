package org.example.kycservice.dto;

import java.util.UUID;

public record KycDocumentDTO(
        UUID id,
        UUID kyc_record_id,
        String type,
        String description,
        String url,
        String uploadedAt
) {
}
