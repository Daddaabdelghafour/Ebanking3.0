package com.ebank.user.service;

import com.ebank.user.dto.EmailUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerEmailUpdate {
       private final  CustomerService customerService;

    @KafkaListener(
            topics = "${email-updated-event}",
            groupId = "user-service-email-update-group"
    )
    public void consumeEmailUpdatedEvent(EmailUpdatedEvent event) {

        log.info("📥 Received EmailUpdatedEvent: {} -> {}",
                event.oldEmail(),
                event.newEmail()
        );

        customerService.updateEmail(event.oldEmail(), event.newEmail());

        log.info("✅ Email updated successfully to {}", event.newEmail());
    }
}
