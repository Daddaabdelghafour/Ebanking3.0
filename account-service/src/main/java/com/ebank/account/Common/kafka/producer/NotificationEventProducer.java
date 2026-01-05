// NotificationEventProducer.java
package com.ebank.account.Common.kafka.producer;

import com.ebank.account.Common.configurations.KafkaTopicsProperties;
import com.ebank.account.Common.kafka.dto.NotificationEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class NotificationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopics;

    public NotificationEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTopicsProperties kafkaTopics
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopics = kafkaTopics;
    }

    public void sendOtpNotification(NotificationEventDTO event) {
        publishEvent(kafkaTopics.getNotificationOtp(), event);
    }

    public void sendTransactionSuccessNotification(NotificationEventDTO event) {
        publishEvent(kafkaTopics.getNotificationTransactionSuccess(), event);
    }

    public void sendTransactionFailedNotification(NotificationEventDTO event) {
        publishEvent(kafkaTopics.getNotificationTransactionFailed(), event);
    }

    public void sendBeneficiaryAddedNotification(NotificationEventDTO event) {
        publishEvent(kafkaTopics.getNotificationBeneficiaryAdded(), event);
    }

    private void publishEvent(String topic, NotificationEventDTO event) {
        try {
            log.info("ATTEMPTING to publish notification to Kafka topic [{}]: recipient={}, type={}", 
                topic, event.getRecipient(), event.getChannel());
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(topic, event.getNotificationId().toString(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("SUCCESSFULLY published notification to topic [{}]: recipient={}, channel={}, partition={}, offset={}", 
                        topic, event.getRecipient(), event.getChannel(), 
                        result.getRecordMetadata().partition(), 
                        result.getRecordMetadata().offset());
                } else {
                    log.error("FAILED to publish notification to topic [{}]: recipient={}, channel={}, error={}", 
                        topic, event.getRecipient(), event.getChannel(), ex.getMessage(), ex);
                }
            });
            
            // CRITICAL: Block and wait for result to ensure message is sent
            future.get(); // This will throw exception if sending fails
            
        } catch (Exception e) {
            log.error("EXCEPTION while publishing notification to Kafka topic [{}]: recipient={}, error={}", 
                topic, event.getRecipient(), e.getMessage(), e);
            // Don't throw exception - notification failure shouldn't break the transaction flow
            // Just log the error
        }
    }
}