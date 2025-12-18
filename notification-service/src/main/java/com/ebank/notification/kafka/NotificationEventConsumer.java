package com.ebank.notification.kafka;

import com.ebank.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "customer-events", groupId = "notification-service-group")
    public void consumeCustomerEvent(Object event) {
        // TODO: Handle customer events (ACCOUNT_CREATED, ACCOUNT_UPDATED, CUSTOMER_CREATED)
        // Send appropriate notifications
        log.info("Received customer event: {}", event);
        // Parse event and send notification
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-service-group")
    public void consumePaymentEvent(Object event) {
        // TODO: Handle payment events (PAYMENT_COMPLETED, PAYMENT_FAILED)
        // Send payment confirmation/failure notifications
        log.info("Received payment event: {}", event);
        // Parse event and send notification
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service-group")
    public void consumeUserEvent(Object event) {
        // TODO: Handle user events (USER_REGISTERED, USER_LOGGED_OUT)
        // Send welcome/logout notifications
        log.info("Received user event: {}", event);
        // Parse event and send notification
    }

    @KafkaListener(topics = "kyc-events", groupId = "notification-service-group")
    public void consumeKycEvent(Object event) {
        // TODO: Handle KYC events (KYC_VERIFIED, KYC_REJECTED)
        // Send KYC status notifications
        log.info("Received KYC event: {}", event);
        // Parse event and send notification
    }
}

