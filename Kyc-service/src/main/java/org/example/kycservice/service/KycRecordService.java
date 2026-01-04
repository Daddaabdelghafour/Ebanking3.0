package org.example.kycservice.service;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.Validate;
import org.example.kycservice.Enum.KycLevel;
import org.example.kycservice.Enum.KycStatus;
import org.example.kycservice.dto.CustomerCreatedEvent;
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
    private final KafkaProducerAccount kafkaProducerAccount;

    public KycRecordService(KycRecordRepository kycRecordRepository,KafkaProducerAccount kafkaProducerAccount, KycStatusResponseMapper kycStatusResponseMapper) {
        this.kycRecordRepository = kycRecordRepository;
        this.kycStatusResponseMapper = kycStatusResponseMapper;
        this.kafkaProducerAccount=kafkaProducerAccount;
    }
    @Transactional
    public void createKycRecord(CustomerCreatedEvent event){
        Validate.notNull(event.userId());
        KycRecord kycRecord = new KycRecord();
        kycRecord.setUserId(event.userId());
        kycRecord.setKycLevel(KycLevel.LEVEL_1);
        kycRecord.setKycStatus(KycStatus.VERIFIED);
        kycRecordRepository.save(kycRecord);
        kafkaProducerAccount.sendCustomerCreatedEvent(event);
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
