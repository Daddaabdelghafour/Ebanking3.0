package com.exemple.authservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.registred-notif-event}")
    private String registredNotifTopic;

    @Value("${kafka.topic.email-virified-notif-event}")
    private String emailVerifiedNotifTopic;

    @Value("${kafka.topic.resend-verified-code-notif-event}")
    private String resendVerifiedCodeNotifTopic;

    @Value("${kafka.topic.forgot-password-notif-event}")
    private String forgotPasswordNotifTopic;

    @Value("${kafka.topic.user-registred-event}")
    private String userRegistredTopic;

    @Value("${kafka.topic.email-updated-event}")
    private String emailUpdatedTopic;

    @Bean
    public NewTopic registredNotifTopic() {
        return TopicBuilder.name(registredNotifTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailVerifiedNotifTopic() {
        return TopicBuilder.name(emailVerifiedNotifTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic resendVerifiedCodeNotifTopic() {
        return TopicBuilder.name(resendVerifiedCodeNotifTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic forgotPasswordNotifTopic() {
        return TopicBuilder.name(forgotPasswordNotifTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userRegistredTopic() {
        return TopicBuilder.name(userRegistredTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailUpdatedTopic() {
        return TopicBuilder.name(emailUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
