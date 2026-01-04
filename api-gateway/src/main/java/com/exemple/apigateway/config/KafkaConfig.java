package com.exemple.apigateway.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.gateway-events}")
    private String gatewayEventsTopic;

    @Value("${kafka.topics.request-logs}")
    private String requestLogsTopic;

    /**
     * Topic pour les événements du Gateway
     */
    @Bean
    public NewTopic gatewayEventsTopic() {
        return TopicBuilder.name(gatewayEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Topic pour les logs des requêtes
     */
    @Bean
    public NewTopic requestLogsTopic() {
        return TopicBuilder.name(requestLogsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
