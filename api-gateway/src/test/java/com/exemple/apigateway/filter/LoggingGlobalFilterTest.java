package com.exemple.apigateway.filter;

import com.exemple.apigateway.model.GatewayEvent;
import com.exemple.apigateway.model.RequestLog;
import com.exemple.apigateway.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingGlobalFilterTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private GatewayFilterChain filterChain;

    private LoggingGlobalFilter loggingGlobalFilter;

    @BeforeEach
    void setUp() {
        loggingGlobalFilter = new LoggingGlobalFilter(kafkaProducerService);
    }

    @Test
    @DisplayName("Doit avoir l'ordre LOWEST_PRECEDENCE")
    void shouldHaveLowestPrecedenceOrder() {
        assertThat(loggingGlobalFilter.getOrder()).isEqualTo(Ordered.LOWEST_PRECEDENCE);
    }

    @Test
    @DisplayName("Doit logger une requête anonyme et envoyer vers Kafka")
    void shouldLogAnonymousRequestAndSendToKafka() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/public/health")
                .header("User-Agent", "TestAgent")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.OK);

        when(filterChain.filter(any())).thenReturn(Mono.empty());

        // When
        loggingGlobalFilter.filter(exchange, filterChain).block();

        // Then
        verify(filterChain).filter(exchange);

        // Vérifie que les événements Kafka sont envoyés
        ArgumentCaptor<RequestLog> logCaptor = ArgumentCaptor.forClass(RequestLog.class);
        verify(kafkaProducerService, atLeastOnce()).sendRequestLog(logCaptor.capture());

        RequestLog capturedLog = logCaptor.getValue();
        assertThat(capturedLog.getMethod()).isEqualTo("GET");
        assertThat(capturedLog.getPath()).isEqualTo("/api/public/health");
        assertThat(capturedLog.getUserId()).isEqualTo("anonymous");
    }

    @Test
    @DisplayName("Doit envoyer un GatewayEvent pour chaque requête")
    void shouldSendGatewayEventForEachRequest() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/api/auth/login")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.OK);

        when(filterChain.filter(any())).thenReturn(Mono.empty());

        // When
        loggingGlobalFilter.filter(exchange, filterChain).block();

        // Then
        ArgumentCaptor<GatewayEvent> eventCaptor = ArgumentCaptor.forClass(GatewayEvent.class);
        verify(kafkaProducerService, atLeastOnce()).sendGatewayEvent(eventCaptor.capture());

        GatewayEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getService()).isEqualTo("api-gateway");
    }
}