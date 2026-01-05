package com.ebank.user.service;

import com.ebank.user.dto.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service

public class KafkaConsumerInscription {
    private final  CustomerService customerService;
   KafkaConsumerInscription(CustomerService customerService)
   {
       this.customerService = customerService;
   }
    @KafkaListener(
            topics = "${user-registred-event}",
            groupId = "user-service-inscription-group"
    )
    public void consumeUserRegisteredEvent(UserRegisteredEvent event) {

        log.info("📥 Received UserRegisteredEvent for email: {}", event.email());
        customerService.createCustomer(event);
        log.info("✅ User registration processed successfully for {}", event.email());
    }
}
