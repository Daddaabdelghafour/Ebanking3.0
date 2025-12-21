package com.ebank.user.service;

import com.ebank.user.dto.CustomerCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final String TOPIC = "customer-created";

    private final KafkaTemplate<String, CustomerCreatedEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, CustomerCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCustomerCreatedEvent(CustomerCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.userId().toString(), event);
        System.out.println("Event sent: " + event);
    }
}
