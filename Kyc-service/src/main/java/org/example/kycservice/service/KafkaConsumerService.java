package org.example.kycservice.service;

import org.example.kycservice.dto.CustomerCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final KycRecordService kycService;

    public KafkaConsumerService(KycRecordService kycService) {
        this.kycService = kycService;
    }

    @KafkaListener(topics = "customer-created", groupId = "kyc-service-group")
    public void handleCustomerCreatedEvent(CustomerCreatedEvent event) {
        System.out.println("Received CustomerCreatedEvent: " + event);
        kycService.createKycRecord(event);
        System.out.println("KYC Record created for user: " + event.userId());
    }
}