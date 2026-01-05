package com.exemple.authservice.service;

import com.exemple.authservice.model.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.registred-notif-event}")
    private String registredNotifTopic;

    @Value("${kafka.topic.email-virified-notif-event}")
    private String emailVerifiedNotifTopic;

    @Value("${kafka.topic.resend-verified-code-notif-event}")
    private String resendVerifiedCodeNotifTopic;

    @Value("${kafka.topic.forgot-password-notif-event}")
    private String forgotPasswordNotifTopic;

    @Value("${kafka.topic.user-registred-event}")
    private String userRegistredTopic;

    @Value("${kafka.topic.email-updated-event}")
    private String emailUpdatedTopic;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Notification: Nouvel utilisateur inscrit
    public void sendNewRegistredEvent(NewRegistredEvent event) {
        logger.info("Sending NewRegistredEvent for email: {}", event.email());
        sendEvent(registredNotifTopic, event.email(), event);
    }

    // Notification: Email vérifié
    public void sendEmailVerificationEvent(EmailVerificationEvent event) {
        logger.info("Sending EmailVerificationEvent for email: {}", event.email());
        sendEvent(emailVerifiedNotifTopic, event.email(), event);
    }

    // Notification: Renvoi du code de vérification
    public void sendResendVerificationCodeEvent(ResendVerificationCodeEvent event) {
        logger.info("Sending ResendVerificationCodeEvent for email: {}", event.email());
        sendEvent(resendVerifiedCodeNotifTopic, event.email(), event);
    }

    // Notification: Mot de passe oublié
    public void sendForgetPasswordEvent(ForgetPasswordEvent event) {
        logger.info("Sending ForgetPasswordEvent for email: {}", event.email());
        sendEvent(forgotPasswordNotifTopic, event.email(), event);
    }

    // User Service: Utilisateur enregistré
    public void sendUserRegisteredEvent(UserRegisteredEvent event) {
        logger.info("Sending UserRegisteredEvent for email: {}", event.email());
        sendEvent(userRegistredTopic, event.keycloakUserId(), event);
    }

    // User Service: Email mis à jour
    public void sendEmailUpdatedEvent(EmailUpdatedEvent event) {
        logger.info("Sending EmailUpdatedEvent from {} to {}", event.oldEmail(), event.newEmail());
        sendEvent(emailUpdatedTopic, event.newEmail(), event);
    }

    // Méthode générique d'envoi
    private void sendEvent(String topic, String key, Object event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("Failed to send event to topic {}: {}", topic, ex.getMessage());
            } else {
                logger.info("Event sent to topic {} with offset {}",
                        topic, result.getRecordMetadata().offset());
            }
        });
    }
}
