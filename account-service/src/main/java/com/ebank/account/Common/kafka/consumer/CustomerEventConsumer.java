package com.ebank.account.Common.kafka.consumer;

import com.ebank.account.Commands.command.CreateAccountCommand;
import com.ebank.account.Commands.util.factory.CommandFactory;
import com.ebank.account.Commands.dto.AccountRequestDTO;
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

    private static final String REQUIRED_ROLE = "CUSTOMER";

    private final CommandGateway commandGateway;

    public CustomerEventConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @KafkaListener(topics = "${kafka.topics.customer-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleCustomerCreated(Map<String, Object> eventData) {
        try {
            log.info("Received customer.created event: {}", eventData);

            // Customer Service payload uses 'userId' (UUID). In this service we call it 'customerId'.
            Object userIdRaw = eventData.get("keycloakUserId");
            if (userIdRaw == null) {
                log.warn("Ignoring customer.created event because 'userId' is missing: {}", eventData);
                return;
            }

            UUID customerId;
            try {
                customerId = UUID.fromString(userIdRaw.toString());
            } catch (IllegalArgumentException ex) {
                log.warn("Ignoring customer.created event because 'userId' is not a valid UUID: userId={}, payload={}", userIdRaw, eventData);
                return;
            }

            // Contract from Customer Service: 'email'
            Object emailRaw = eventData.get("email");
            if (emailRaw == null || emailRaw.toString().isBlank()) {
                log.warn("Ignoring customer.created event because 'email' is missing/blank: customerId={}, payload={}", customerId, eventData);
                return;
            }
            String email = emailRaw.toString();

            // Contract from Customer Service: 'role' must be CUSTOMER
            Object roleRaw = eventData.get("role");
            if (roleRaw == null || roleRaw.toString().isBlank()) {
                log.warn("Ignoring customer.created event because 'role' is missing/blank: customerId={}, payload={}", customerId, eventData);
                return;
            }
            String role = roleRaw.toString();

            if (!REQUIRED_ROLE.equalsIgnoreCase(role.trim())) {
                log.info("Ignoring customer.created event because role is not CUSTOMER: customerId={}, role={}", customerId, role);
                return;
            }

            // Ignore other fields; accounts are created with an initial balance of 0.
            BigDecimal initialBalance = BigDecimal.ZERO;

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
