package com.ebank.account.Common.kafka.consumer;

import com.ebank.account.Commands.command.SuspendAccountCommand;
import com.ebank.account.Commands.util.factory.CommandFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class FraudEventConsumer {

    private final CommandGateway commandGateway;
    private final ObjectMapper objectMapper;

    public FraudEventConsumer(CommandGateway commandGateway, ObjectMapper objectMapper) {
        this.commandGateway = commandGateway;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topics.fraud-detected}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleFraudDetected(Map<String, Object> eventData) {
        try {
            log.info("Received fraud.detected event: {}", eventData);
            
            UUID accountId = UUID.fromString(eventData.get("accountId").toString());
            String reason = eventData.getOrDefault("reason", "Fraudulent activity detected").toString();
            String severity = eventData.getOrDefault("severity", "HIGH").toString();
            
            // Suspend account immediately
            SuspendAccountCommand command = CommandFactory.suspendAccountCommand(accountId);
            
            commandGateway.send(command)
                .thenAccept(result -> log.warn("Account {} suspended due to fraud: {} (severity: {})", 
                    accountId, reason, severity))
                .exceptionally(ex -> {
                    log.error("Failed to suspend account {} after fraud detection: {}", 
                        accountId, ex.getMessage());
                    return null;
                });
                
        } catch (Exception e) {
            log.error("Error processing fraud.detected event: {}", e.getMessage(), e);
        }
    }
}
