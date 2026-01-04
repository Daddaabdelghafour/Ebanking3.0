package com.exemple.apigateway.service;


import com.exemple.apigateway.model.GatewayEvent;
import com.exemple.apigateway.model.RequestLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService. class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.gateway-events}")
    private String gatewayEventsTopic;

    @Value("${kafka.topics.request-logs}")
    private String requestLogsTopic;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Envoie un événement du Gateway vers Kafka
     */
    public void sendGatewayEvent(GatewayEvent event) {
        logger.info("Sending gateway event: {} for user: {}", event.getEventType(), event.getUserId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(gatewayEventsTopic, event.getEventId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Event sent successfully: {} to partition: {}",
                        event.getEventId(),
                        result.getRecordMetadata().partition());
            } else {
                logger.error("Failed to send event: {}", event.getEventId(), ex);
            }
        });
    }

    /**
     * Envoie un log de requête vers Kafka
     */
    public void sendRequestLog(RequestLog requestLog) {
        logger.debug("Sending request log: {} {} - Status: {}",
                requestLog.getMethod(),
                requestLog.getPath(),
                requestLog. getStatusCode());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(requestLogsTopic, requestLog.getRequestId(), requestLog);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.debug("Request log sent successfully: {}", requestLog.getRequestId());
            } else {
                logger.error("Failed to send request log: {}", requestLog.getRequestId(), ex);
            }
        });
    }
}
