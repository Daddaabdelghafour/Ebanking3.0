package com.ebank.account.Common.kafka.consumer;

import com.ebank.account.Commands.command.CreateAccountCommand;
import com.ebank.account.Commands.util.factory.CommandFactory;
import com.ebank.account.Commands.dto.AccountRequestDTO;
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

            Object customerIdRaw = eventData.get("customerId");
            if (customerIdRaw == null) {
                log.warn("Ignoring customer.created event because 'customerId' is missing: {}", eventData);
                return;
            }
            UUID customerId = UUID.fromString(customerIdRaw.toString());

            // Contract: external service sends customerEmail
            // Backward compatibility: accept 'email' as well
            Object emailRaw = eventData.getOrDefault("customerEmail", eventData.get("email"));
            if (emailRaw == null) {
                log.warn("Ignoring customer.created event because 'customerEmail' (or legacy 'email') is missing: customerId={}, payload={}", customerId, eventData);
                return;
            }
            String email = emailRaw.toString();

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
}
