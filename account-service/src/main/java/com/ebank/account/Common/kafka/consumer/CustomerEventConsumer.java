package com.ebank.account.Common.kafka.consumer;

import com.ebank.account.Commands.command.CreateAccountCommand;
import com.ebank.account.Commands.command.DeleteAccountCommand;
import com.ebank.account.Commands.util.factory.CommandFactory;
import com.ebank.account.Common.enums.AccountStatus;
import com.ebank.account.dto.AccountRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CustomerEventConsumer {

    private final CommandGateway commandGateway;
    private final ObjectMapper objectMapper;

    public CustomerEventConsumer(CommandGateway commandGateway, ObjectMapper objectMapper) {
        this.commandGateway = commandGateway;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topics.customer-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleCustomerCreated(Map<String, Object> eventData) {
        try {
            log.info("Received customer.created event: {}", eventData);
            
            UUID customerId = UUID.fromString(eventData.get("customerId").toString());
            String email = eventData.get("email").toString();
            BigDecimal initialBalance = eventData.containsKey("initialBalance") 
                ? new BigDecimal(eventData.get("initialBalance").toString()) 
                : BigDecimal.ZERO;

            // Create account for new customer
            AccountRequestDTO dto = new AccountRequestDTO(customerId, email, initialBalance);
            CreateAccountCommand command = CommandFactory.createAccountCommand(dto);
            
            commandGateway.send(command)
                .thenAccept(result -> log.info("Account created for customer: {}", customerId))
                .exceptionally(ex -> {
                    log.error("Failed to create account for customer {}: {}", customerId, ex.getMessage());
                    return null;
                });
                
        } catch (Exception e) {
            log.error("Error processing customer.created event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.customer-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleCustomerDeleted(Map<String, Object> eventData) {
        try {
            log.info("Received customer.deleted event: {}", eventData);
            
            UUID customerId = UUID.fromString(eventData.get("customerId").toString());
            
            // Delete associated account (must have zero balance)
            // In a real scenario, you'd query for the account first
            log.warn("Customer deleted event received for customerId: {}. Manual account deletion may be required.", customerId);
            
        } catch (Exception e) {
            log.error("Error processing customer.deleted event: {}", e.getMessage(), e);
        }
    }
}
