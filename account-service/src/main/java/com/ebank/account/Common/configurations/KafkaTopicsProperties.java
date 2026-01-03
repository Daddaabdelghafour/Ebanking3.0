package com.ebank.account.Common.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
@Getter
@Setter
public class KafkaTopicsProperties {
    
    // Producer Topics - Account Events
    private String accountCreated;
    private String accountActivated;
    private String accountSuspended;
    private String accountDeleted;
    private String accountCredited;
    private String accountDebited;
    private String accountTransferred;

    // Producer Topics - Notification Events
    private String notificationOtp;
    private String notificationTransactionSuccess;
    private String notificationTransactionFailed;
    private String notificationBeneficiaryAdded;
    
    // Consumer Topics - External Events
    private String customerCreated;
    private String customerDeleted;
    private String paymentCompleted;
    private String paymentFailed;
    private String fraudDetected;
}
