package org.example.kycservice.mapper;

import org.example.kycservice.dto.KycRecordDTO;
import org.example.kycservice.entity.KycRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {KycDocumentMapper.class})
public interface KycRecordMapper {

    KycRecordDTO toDTO(KycRecord entity);

    KycRecord toEntity(KycRecordDTO dto);
}