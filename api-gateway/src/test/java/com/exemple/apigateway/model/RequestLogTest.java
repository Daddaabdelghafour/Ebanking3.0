package com.exemple.apigateway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLogTest {

    @Test
    @DisplayName("Doit créer un RequestLog avec le builder")
    void shouldCreateRequestLogWithBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        RequestLog log = RequestLog.builder()
                .requestId("req-123")
                .method("GET")
                .path("/api/users/profile")
                .userId("user-456")
                .statusCode(200)
                .duration(150L)
                .timestamp(now)
                .userAgent("Mozilla/5.0")
                .ipAddress("192.168.1.1")
                .build();

        // Then
        assertThat(log.getRequestId()).isEqualTo("req-123");
        assertThat(log.getMethod()).isEqualTo("GET");
        assertThat(log.getPath()).isEqualTo("/api/users/profile");
        assertThat(log.getUserId()).isEqualTo("user-456");
        assertThat(log.getStatusCode()).isEqualTo(200);
        assertThat(log.getDuration()).isEqualTo(150L);
        assertThat(log.getTimestamp()).isEqualTo(now);
        assertThat(log.getUserAgent()).isEqualTo("Mozilla/5.0");
        assertThat(log.getIpAddress()).isEqualTo("192.168.1.1");
    }
}