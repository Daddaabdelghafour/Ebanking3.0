package org.example.kycservice.service;

import jakarta.transaction.Transactional;
import org.apache.commons.lang.Validate;
import org.example.kycservice.Enum.KycLevel;
import org.example.kycservice.Enum.KycStatus;
import org.example.kycservice.dto.KycStatusResponseDTO;
import org.example.kycservice.entity.KycRecord;
import org.example.kycservice.mapper.KycStatusResponseMapper;
import org.example.kycservice.repo.KycRecordRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class KycRecordService {
    private final KycRecordRepository kycRecordRepository;
    private final KycStatusResponseMapper kycStatusResponseMapper  ;

    public KycRecordService(KycRecordRepository kycRecordRepository, KycStatusResponseMapper kycStatusResponseMapper) {
        this.kycRecordRepository = kycRecordRepository;
        this.kycStatusResponseMapper = kycStatusResponseMapper;
    }
    @Transactional
    public void createKycRecord(UUID userId){
        Validate.notNull(userId);
        KycRecord kycRecord = new KycRecord();
        kycRecord.setUserId(userId);
        kycRecord.setKycLevel(KycLevel.LEVEL_0);
        kycRecord.setKycStatus(KycStatus.PENDING);
        kycRecordRepository.save(kycRecord);
    }
    @Transactional
    public KycStatusResponseDTO getStatusByUserId(UUID userId){
        KycRecord kycRecord = kycRecordRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("KYC record not found for userId: " + userId));
        return kycStatusResponseMapper.toDTO(kycRecord);
    }
    @Transactional
    public void updateKycStatus(UUID userId, KycStatus kycStatus){
        KycRecord kycRecord = kycRecordRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("KYC record not found for userId: " + userId));
        kycRecord.setKycStatus(kycStatus);
    }
    @Transactional
    public void updateKycRecord(UUID userId, KycLevel kycLevel){
        KycRecord kycRecord = kycRecordRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("KYC record not found for userId: " + userId));
        kycRecord.setKycLevel(kycLevel);
    }





}
