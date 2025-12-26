package org.example.kycservice.mapper;

import org.example.kycservice.dto.KycDocumentDTO;
import org.example.kycservice.entity.KycDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;@Mapper(componentModel = "spring")
public interface KycDocumentMapper {

    @Mapping(source = "kycRecord.id", target = "kyc_record_id")
    KycDocumentDTO toDto(KycDocument entity);

    @Mapping(source = "kyc_record_id", target = "kycRecord.id")
    KycDocument toEntity(KycDocumentDTO dto);
}
