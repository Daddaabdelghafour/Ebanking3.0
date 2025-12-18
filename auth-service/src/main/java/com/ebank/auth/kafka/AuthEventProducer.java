package com.ebank.auth.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_REGISTERED_TOPIC = "user-events";
    private static final String USER_LOGGED_OUT_TOPIC = "user-events";

    public void publishUserRegistered(String userId, String email) {
        // TODO: Publish USER_REGISTERED event
        // Create event object with userId, email, timestamp
        log.info("Publishing USER_REGISTERED event for user: {}", userId);
        // kafkaTemplate.send(USER_REGISTERED_TOPIC, event);
    }

    public void publishUserLoggedOut(String userId) {
        // TODO: Publish USER_LOGGED_OUT event
        log.info("Publishing USER_LOGGED_OUT event for user: {}", userId);
        // kafkaTemplate.send(USER_LOGGED_OUT_TOPIC, event);
    }
}

