package com.ebank.account.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountEventConsumer {

    @KafkaListener(topics = "user-events", groupId = "account-service-group")
    public void consumeUserEvent(Object event) {
        // TODO: Handle user events (USER_REGISTERED, CUSTOMER_CREATED)
        // Optionally create default account for new customers
        log.info("Received user event: {}", event);
    }

    @KafkaListener(topics = "payment-events", groupId = "account-service-group")
    public void consumePaymentEvent(Object event) {
        // TODO: Handle payment events (PAYMENT_COMPLETED, PAYMENT_FAILED)
        // Update account balance accordingly
        log.info("Received payment event: {}", event);
    }
}

