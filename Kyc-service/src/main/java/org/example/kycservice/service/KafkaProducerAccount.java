package org.example.kycservice.service;

import org.example.kycservice.dto.CustomerCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service

public class KafkaProducerAccount {
    private static final String TOPIC = "customer.created";

    private final KafkaTemplate<String, CustomerCreatedEvent> kafkaTemplate;


    public KafkaProducerAccount(KafkaTemplate<String, CustomerCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCustomerCreatedEvent(CustomerCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.userId().toString(), event);
        System.out.println("Event sent: " + event);
    }
}
