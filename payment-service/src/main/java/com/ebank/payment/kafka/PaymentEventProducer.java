package com.ebank.payment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PAYMENT_EVENTS_TOPIC = "payment-events";

    public void publishPaymentCompleted(UUID paymentId, UUID sourceAccountId, UUID targetAccountId, Double amount) {
        // TODO: Publish PAYMENT_COMPLETED event to payment-events topic
        // Create event object with paymentId, sourceAccountId, targetAccountId, amount, timestamp
        log.info("Publishing PAYMENT_COMPLETED event for payment: {}", paymentId);
        // kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, event);
    }

    public void publishPaymentFailed(UUID paymentId, String failureReason) {
        // TODO: Publish PAYMENT_FAILED event
        log.info("Publishing PAYMENT_FAILED event for payment: {}", paymentId);
        // kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, event);
    }

    public void publishPaymentCancelled(UUID paymentId) {
        // TODO: Publish PAYMENT_CANCELLED event
        log.info("Publishing PAYMENT_CANCELLED event for payment: {}", paymentId);
        // kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, event);
    }
}

