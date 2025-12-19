package org.example.kycservice.mapper;

import org.example.kycservice.dto.KycStatusResponseDTO;
import org.example.kycservice.entity.KycRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {KycDocumentMapper.class})
public interface KycStatusResponseMapper {

    KycStatusResponseMapper INSTANCE = Mappers.getMapper(KycStatusResponseMapper.class);

    @Mapping(source = "kycLevel", target = "kycLevel")     // peut être string
    @Mapping(source = "kycStatus", target = "kycStatus")   // peut être string
    @Mapping(source = "documents", target = "documents")
    KycStatusResponseDTO toDTO(KycRecord record);
}
