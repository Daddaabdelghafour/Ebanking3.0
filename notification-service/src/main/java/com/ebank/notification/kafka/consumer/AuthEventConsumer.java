package com.ebank.notification.kafka.consumer;

import com.ebank.notification.kafka.event.*;
import com.ebank.notification.model.email.AuthEmailInfo;
import com.ebank.notification.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    /**
     * Correction de l'erreur LocalDateTime :
     * On enregistre le module JSR310 dès que le composant est prêt.
     */
    @PostConstruct
    public void configureObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    // 1. Inscription : new-user-registred
    @KafkaListener(topics = "${kafka.topic.registred-notif-event}")
    public void consumeRegistration(ConsumerRecord<String, Object> record) {
        try {
            log.info("📩 Réception : Nouvel utilisateur inscrit");
            NewRegistredEvent event = objectMapper.convertValue(record.value(), NewRegistredEvent.class);
            
            AuthEmailInfo info = AuthEmailInfo.builder()
                    .to(event.email()) // Utilise ton format record/méthode
                    .userName(event.firstName() + " " + event.lastName())
                    .code(event.verificationCode())
                    .validity(event.validityPeriod())
                    .templateName("welcome-code")
                    .subject("Bienvenue ! Activez votre compte")
                    .build();
            
            emailService.sendAuthEmail(info);
        } catch (Exception e) {
            log.error("❌ Erreur lors du traitement de l'inscription: {}", e.getMessage());
        }
    }

    // 2. Email Vérifié : email-verified
    @KafkaListener(topics = "${kafka.topic.email-virified-notif-event}")
    public void consumeVerificationSuccess(ConsumerRecord<String, Object> record) {
        try {
            log.info("📩 Réception : Email vérifié avec succès");
            EmailVerifiedEvent event = objectMapper.convertValue(record.value(), EmailVerifiedEvent.class);
            
            if (event.verified()) {
                AuthEmailInfo info = AuthEmailInfo.builder()
                        .to(event.email())
                        .templateName("email-verified-success")
                        .subject("Votre compte est maintenant activé")
                        .build();
                
                emailService.sendAuthEmail(info);
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification d'email: {}", e.getMessage());
        }
    }

    // 3. Renvoi du Code : resend-verification-code
    @KafkaListener(topics = "${kafka.topic.resend-verified-code-notif-event}")
    public void consumeResendCode(ConsumerRecord<String, Object> record) {
        try {
            log.info("📩 Réception : Demande de renvoi de code");
            ResendVerificationCodeEvent event = objectMapper.convertValue(record.value(), ResendVerificationCodeEvent.class);
            
            AuthEmailInfo info = AuthEmailInfo.builder()
                    .to(event.email())
                    .code(event.verificationCode())
                    .templateName("resend-code")
                    .subject("Nouveau code de vérification")
                    .build();
            
            emailService.sendAuthEmail(info);
        } catch (Exception e) {
            log.error("❌ Erreur lors du renvoi de code: {}", e.getMessage());
        }
    }

    // 4. Mot de passe oublié : forgot-password
    @KafkaListener(topics = "${kafka.topic.forgot-password-notif-event}")
    public void consumeForgotPassword(ConsumerRecord<String, Object> record) {
        try {
            log.info("📩 Réception : Mot de passe oublié");
            ForgetPasswordEvent event = objectMapper.convertValue(record.value(), ForgetPasswordEvent.class);
            
            AuthEmailInfo info = AuthEmailInfo.builder()
                    .to(event.email())
                    .code(event.resetCode())
                    .templateName("forgot-password")
                    .subject("Réinitialisation de votre mot de passe")
                    .build();
            
            emailService.sendAuthEmail(info);
        } catch (Exception e) {
            log.error("❌ Erreur lors du mot de passe oublié: {}", e.getMessage());
        }
    }
}