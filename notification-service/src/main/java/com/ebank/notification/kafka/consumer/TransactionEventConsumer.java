package com.ebank.notification.kafka.consumer;

import com.ebank.notification.kafka.event.*;
import com.ebank.notification.model.email.AuthEmailInfo;
import com.ebank.notification.model.email.NotificationEmailInfo;
import com.ebank.notification.service.EmailService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "account.created")
    public void consumeAccount(ConsumerRecord<String, Object> record) {
        try {
            // Conversion du JSON en Objet
            AccountCreatedEvent event = objectMapper.convertValue(record.value(), AccountCreatedEvent.class);
            
            // CRUCIAL : On transforme l'objet en Map pour injecter (iban, accountNumber, balance) dans le HTML
            Map<String, Object> metadata = objectMapper.convertValue(event, new TypeReference<Map<String, Object>>() {});

            NotificationEmailInfo info = NotificationEmailInfo.builder()
                    .to(event.email())
                    .subject("Ouverture de compte confirmée")
                    .templateName("account-created")
                    .message("Votre compte a été créé avec succès.")
                    .metadata(metadata) 
                    .build();

            emailService.sendNotificationEmail(info);
            log.info("✅ Notification de création de compte envoyée à {}", event.email());
        } catch (Exception e) {
            log.error("❌ Erreur lors du traitement account.created", e);
        }
    }

    @KafkaListener(topics = {
        "${kafka.topics.notification-otp}",
        "${kafka.topics.notification-transaction-success}",
        "${kafka.topics.notification-transaction-failed}",
        "${kafka.topics.notification-beneficiary-added}"
    })
    public void consumeGeneric(ConsumerRecord<String, Object> record) {
        try {
            GenericNotificationEvent event = objectMapper.convertValue(record.value(), GenericNotificationEvent.class);
            String template = determineTemplate(record.topic());
            
            NotificationEmailInfo info = NotificationEmailInfo.builder()
                    .to(event.recipient())
                    .subject(event.subject())
                    .message(event.message())
                    .metadata(event.metadata()) // Contient amount, currency, beneficiaryName, etc.
                    .templateName(template)
                    .build();

            emailService.sendNotificationEmail(info);
            log.info("✅ Notification {} envoyée à {}", template, event.recipient());
        } catch (Exception e) {
            log.error("❌ Erreur sur le topic {}", record.topic(), e);
        }
    }

    private String determineTemplate(String topic) {
        if (topic.contains("otp")) return "transaction-otp";
        if (topic.contains("success")) return "transaction-success";
        if (topic.contains("failed")) return "transaction-failed";
        if (topic.contains("beneficiary")) return "beneficiary-added";
        return "generic-notification";
    }
}