package com.ebank.auth.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthEventConsumer {

    @KafkaListener(topics = "kyc-events", groupId = "auth-service-group")
    public void consumeKycEvent(Object event) {
        // TODO: Handle KYC events (KYC_VERIFIED, KYC_REJECTED)
        // Update user verification status
        log.info("Received KYC event: {}", event);
    }
}

