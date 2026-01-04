package com.exemple.apigateway.service;

import com.exemple.apigateway.model.GatewayEvent;
import com.exemple.apigateway.model.RequestLog;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        kafkaProducerService = new KafkaProducerService(kafkaTemplate);
        // Injecter les valeurs des topics via ReflectionTestUtils
        ReflectionTestUtils.setField(kafkaProducerService, "gatewayEventsTopic", "gateway-events");
        ReflectionTestUtils.setField(kafkaProducerService, "requestLogsTopic", "request-logs");
    }

    @Test
    @DisplayName("Doit envoyer un GatewayEvent vers Kafka")
    void shouldSendGatewayEvent() {
        // Given
        GatewayEvent event = new GatewayEvent("REQUEST_RECEIVED", "user-123", "api-gateway", "Test");

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("gateway-events", 0), 0L, 0, 0L, 0, 0);
        SendResult<String, Object> sendResult = new SendResult<>(
                new ProducerRecord<>("gateway-events", event.getEventId(), event), metadata);
        future.complete(sendResult);

        when(kafkaTemplate.send(eq("gateway-events"), any(String.class), any(GatewayEvent.class)))
                .thenReturn(future);

        // When
        kafkaProducerService.sendGatewayEvent(event);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<GatewayEvent> eventCaptor = ArgumentCaptor.forClass(GatewayEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("gateway-events");
        assertThat(keyCaptor.getValue()).isEqualTo(event.getEventId());
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo("REQUEST_RECEIVED");
    }

    @Test
    @DisplayName("Doit envoyer un RequestLog vers Kafka")
    void shouldSendRequestLog() {
        // Given
        RequestLog log = RequestLog.builder()
                .requestId("req-123")
                .method("GET")
                .path("/api/users")
                .userId("user-456")
                .statusCode(200)
                .duration(100L)
                .timestamp(LocalDateTime.now())
                .build();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("request-logs", 0), 0L, 0, 0L, 0, 0);
        SendResult<String, Object> sendResult = new SendResult<>(
                new ProducerRecord<>("request-logs", log.getRequestId(), log), metadata);
        future.complete(sendResult);

        when(kafkaTemplate.send(eq("request-logs"), any(String.class), any(RequestLog.class)))
                .thenReturn(future);

        // When
        kafkaProducerService.sendRequestLog(log);

        // Then
        verify(kafkaTemplate).send(eq("request-logs"), eq("req-123"), eq(log));
    }

    @Test
    @DisplayName("Doit gérer l'erreur d'envoi Kafka")
    void shouldHandleKafkaSendError() {
        // Given
        GatewayEvent event = new GatewayEvent("REQUEST_RECEIVED", "user-123", "api-gateway", "Test");

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka unavailable"));

        when(kafkaTemplate.send(any(String.class), any(String.class), any(GatewayEvent.class)))
                .thenReturn(future);

        // When - Then (ne doit pas lever d'exception)
        kafkaProducerService.sendGatewayEvent(event);

        verify(kafkaTemplate).send(eq("gateway-events"), any(String.class), eq(event));
    }
}
