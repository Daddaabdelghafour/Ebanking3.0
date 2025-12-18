package com.ebank.account.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String CUSTOMER_EVENTS_TOPIC = "customer-events";

    public void publishAccountCreated(UUID accountId, UUID customerId) {
        // TODO: Publish ACCOUNT_CREATED event to customer-events topic
        // Create event object with accountId, customerId, timestamp
        log.info("Publishing ACCOUNT_CREATED event for account: {}", accountId);
        // kafkaTemplate.send(CUSTOMER_EVENTS_TOPIC, event);
    }

    public void publishAccountUpdated(UUID accountId, UUID customerId, Double newBalance) {
        // TODO: Publish ACCOUNT_UPDATED event
        log.info("Publishing ACCOUNT_UPDATED event for account: {}", accountId);
        // kafkaTemplate.send(CUSTOMER_EVENTS_TOPIC, event);
    }
}

