package com.ebank.account.Common.kafka.producer;

import com.ebank.account.Common.configurations.KafkaTopicsProperties;
import com.ebank.account.Common.kafka.dto.AccountEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AccountEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopics;

    public AccountEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTopicsProperties kafkaTopics
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopics = kafkaTopics;
    }

    public void publishAccountCreated(AccountEventDTO event) {
        publishEvent(kafkaTopics.getAccountCreated(), event);
    }

    public void publishAccountActivated(AccountEventDTO event) {
        publishEvent(kafkaTopics.getAccountActivated(), event);
    }

    public void publishAccountSuspended(AccountEventDTO event) {
        publishEvent(kafkaTopics.getAccountSuspended(), event);
    }

    public void publishAccountDeleted(AccountEventDTO event) {
        publishEvent(kafkaTopics.getAccountDeleted(), event);
    }

    public void publishAccountCredited(AccountEventDTO event) {
        publishEvent(kafkaTopics.getAccountCredited(), event);
    }

    public void publishAccountDebited(AccountEventDTO event) {
        publishEvent(kafkaTopics.getAccountDebited(), event);
    }

    public void publishAccountTransferred(AccountEventDTO event) {
        publishEvent(kafkaTopics.getAccountTransferred(), event);
    }

    private void publishEvent(String topic, AccountEventDTO event) {
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(topic, event.getAccountId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published event to topic [{}]: accountId={}, eventType={}", 
                    topic, event.getAccountId(), event.getEventType());
            } else {
                log.error("Failed to publish event to topic [{}]: accountId={}, eventType={}, error={}", 
                    topic, event.getAccountId(), event.getEventType(), ex.getMessage());
            }
        });
    }
}
