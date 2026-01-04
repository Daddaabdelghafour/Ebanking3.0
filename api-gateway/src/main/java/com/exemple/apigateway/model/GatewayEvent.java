package com.exemple.apigateway.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatewayEvent {

    private String eventId;
    private String eventType;
    private String userId;
    private String service;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Constructeur qui génère automatiquement l'ID et le timestamp
     */
    public GatewayEvent(String eventType, String userId, String service, String message) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this. userId = userId;
        this. service = service;
        this. message = message;
        this. timestamp = LocalDateTime.now();
    }
}
