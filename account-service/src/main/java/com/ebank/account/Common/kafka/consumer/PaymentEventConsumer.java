package com.ebank.account.Common.kafka.consumer;

import com.ebank.account.Commands.command.CreditAccountCommand;
import com.ebank.account.Commands.util.factory.CommandFactory;
import com.ebank.account.dto.OperationRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PaymentEventConsumer {

    private final CommandGateway commandGateway;
    private final ObjectMapper objectMapper;

    public PaymentEventConsumer(CommandGateway commandGateway, ObjectMapper objectMapper) {
        this.commandGateway = commandGateway;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topics.payment-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentCompleted(Map<String, Object> eventData) {
        try {
            log.info("Received payment.completed event: {}", eventData);
            
            UUID accountId = UUID.fromString(eventData.get("accountId").toString());
            BigDecimal amount = new BigDecimal(eventData.get("amount").toString());
            String description = eventData.getOrDefault("description", "Payment completed").toString();
            String reference = eventData.getOrDefault("transactionReference", "N/A").toString();
            
            // Credit account with payment amount
            OperationRequestDTO dto = new OperationRequestDTO(accountId ,amount, description + " - Ref: " + reference);
            CreditAccountCommand command = CommandFactory.creditAccountCommand(accountId, dto);
            
            commandGateway.send(command)
                .thenAccept(result -> log.info("Account {} credited with payment: {}", accountId, amount))
                .exceptionally(ex -> {
                    log.error("Failed to credit account {} with payment: {}", accountId, ex.getMessage());
                    return null;
                });
                
        } catch (Exception e) {
            log.error("Error processing payment.completed event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.payment-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentFailed(Map<String, Object> eventData) {
        try {
            log.info("Received payment.failed event: {}", eventData);
            
            UUID accountId = UUID.fromString(eventData.get("accountId").toString());
            
            // Could trigger reversal logic here if needed
            log.warn("Payment failed for account: {}. Manual intervention may be required.", accountId);
            
        } catch (Exception e) {
            log.error("Error processing payment.failed event: {}", e.getMessage(), e);
        }
    }
}
