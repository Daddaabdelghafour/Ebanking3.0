package com.exemple.authservice.service;

import com.exemple.authservice.model.event.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        kafkaProducerService = new KafkaProducerService(kafkaTemplate);

        // Injecter les noms des topics via reflection
        ReflectionTestUtils.setField(kafkaProducerService, "registredNotifTopic", "registred-notif");
        ReflectionTestUtils.setField(kafkaProducerService, "emailVerifiedNotifTopic", "email-verified-notif");
        ReflectionTestUtils.setField(kafkaProducerService, "resendVerifiedCodeNotifTopic", "resend-code-notif");
        ReflectionTestUtils.setField(kafkaProducerService, "forgotPasswordNotifTopic", "forgot-password-notif");
        ReflectionTestUtils.setField(kafkaProducerService, "userRegistredTopic", "user-registered");
        ReflectionTestUtils.setField(kafkaProducerService, "emailUpdatedTopic", "email-updated");
    }

    private CompletableFuture<SendResult<String, Object>> mockKafkaSuccess(String topic) {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        RecordMetadata metadata = new RecordMetadata(new TopicPartition(topic, 0), 0L, 0, 0L, 0, 0);
        SendResult<String, Object> result = new SendResult<>(new ProducerRecord<>(topic, "key", "value"), metadata);
        future.complete(result);
        return future;
    }

    // ==================== sendNewRegistredEvent ====================

    @Test
    @DisplayName("Doit envoyer NewRegistredEvent vers Kafka")
    void shouldSendNewRegistredEvent() {
        // Given
        NewRegistredEvent event = NewRegistredEvent.builder()
                .eventId("event-123")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .verificationCode("123456")
                .validityPeriod("15 minutes")
                .timestamp(LocalDateTime.now())
                .build();

        when(kafkaTemplate.send(eq("registred-notif"), any(String.class), any(NewRegistredEvent.class)))
                .thenReturn(mockKafkaSuccess("registred-notif"));

        // When
        kafkaProducerService.sendNewRegistredEvent(event);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<NewRegistredEvent> eventCaptor = ArgumentCaptor.forClass(NewRegistredEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("registred-notif");
        assertThat(keyCaptor.getValue()).isEqualTo("test@example.com");
        assertThat(eventCaptor.getValue().email()).isEqualTo("test@example.com");
    }

    // ==================== sendEmailVerificationEvent ====================

    @Test
    @DisplayName("Doit envoyer EmailVerificationEvent vers Kafka")
    void shouldSendEmailVerificationEvent() {
        // Given
        EmailVerificationEvent event = EmailVerificationEvent.builder()
                .eventId("event-456")
                .email("user@example.com")
                .verified(true)
                .timestamp(LocalDateTime.now())
                .build();

        when(kafkaTemplate.send(eq("email-verified-notif"), any(String.class), any(EmailVerificationEvent.class)))
                .thenReturn(mockKafkaSuccess("email-verified-notif"));

        // When
        kafkaProducerService.sendEmailVerificationEvent(event);

        // Then
        verify(kafkaTemplate).send(eq("email-verified-notif"), eq("user@example.com"), eq(event));
    }

    // ==================== sendUserRegisteredEvent ====================

    @Test
    @DisplayName("Doit envoyer UserRegisteredEvent vers Kafka avec keycloakUserId comme clé")
    void shouldSendUserRegisteredEventWithKeycloakIdAsKey() {
        // Given
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .eventId("event-789")
                .keycloakUserId("kc-user-123")
                .email("new@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .timestamp(LocalDateTime.now())
                .build();

        when(kafkaTemplate.send(eq("user-registered"), any(String.class), any(UserRegisteredEvent.class)))
                .thenReturn(mockKafkaSuccess("user-registered"));

        // When
        kafkaProducerService.sendUserRegisteredEvent(event);

        // Then
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("user-registered"), keyCaptor.capture(), eq(event));

        assertThat(keyCaptor.getValue()).isEqualTo("kc-user-123"); // clé = keycloakUserId
    }

    // ==================== sendForgetPasswordEvent ====================

    @Test
    @DisplayName("Doit envoyer ForgetPasswordEvent vers Kafka")
    void shouldSendForgetPasswordEvent() {
        // Given
        ForgetPasswordEvent event = ForgetPasswordEvent.builder()
                .eventId("event-forgot")
                .email("forgot@example.com")
                .resetCode("654321")
                .timestamp(LocalDateTime.now())
                .build();

        when(kafkaTemplate.send(eq("forgot-password-notif"), any(String.class), any(ForgetPasswordEvent.class)))
                .thenReturn(mockKafkaSuccess("forgot-password-notif"));

        // When
        kafkaProducerService.sendForgetPasswordEvent(event);

        // Then
        verify(kafkaTemplate).send(eq("forgot-password-notif"), eq("forgot@example.com"), eq(event));
    }

    // ==================== sendEmailUpdatedEvent ====================

    @Test
    @DisplayName("Doit envoyer EmailUpdatedEvent vers Kafka")
    void shouldSendEmailUpdatedEvent() {
        // Given
        EmailUpdatedEvent event = EmailUpdatedEvent.builder()
                .eventId("event-update")
                .oldEmail("old@example.com")
                .newEmail("new@example.com")
                .timestamp(LocalDateTime.now())
                .build();

        when(kafkaTemplate.send(eq("email-updated"), any(String.class), any(EmailUpdatedEvent.class)))
                .thenReturn(mockKafkaSuccess("email-updated"));

        // When
        kafkaProducerService.sendEmailUpdatedEvent(event);

        // Then
        verify(kafkaTemplate).send(eq("email-updated"), eq("new@example.com"), eq(event));
    }

    // ==================== Test erreur Kafka ====================

    @Test
    @DisplayName("Doit gérer l'erreur d'envoi Kafka sans exception")
    void shouldHandleKafkaErrorWithoutException() {
        // Given
        NewRegistredEvent event = NewRegistredEvent.builder()
                .eventId("event-error")
                .email("error@example.com")
                .firstName("Error")
                .lastName("Test")
                .verificationCode("000000")
                .validityPeriod("15 minutes")
                .timestamp(LocalDateTime.now())
                .build();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka unavailable"));

        when(kafkaTemplate.send(any(String.class), any(String.class), any()))
                .thenReturn(future);

        // When - Then (ne doit pas lever d'exception)
        kafkaProducerService.sendNewRegistredEvent(event);

        verify(kafkaTemplate).send(eq("registred-notif"), eq("error@example.com"), eq(event));
    }
}