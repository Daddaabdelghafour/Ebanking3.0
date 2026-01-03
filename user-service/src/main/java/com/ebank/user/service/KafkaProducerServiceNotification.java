package com.ebank.user.service;

import com.ebank.user.dto.CustomerCreatedEvent;
import com.ebank.user.dto.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceNotification {
    private static final String TOPIC = "Notification_??";
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    public KafkaProducerServiceNotification(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendCustomerCreatedEvent(NotificationEvent event) {
        kafkaTemplate.send(TOPIC, event.userId().toString(), event);
        System.out.println("Event sent: " + event);
    }
}
