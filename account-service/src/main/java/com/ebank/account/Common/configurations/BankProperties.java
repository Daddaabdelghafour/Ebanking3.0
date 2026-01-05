package com.ebank.account.Common.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bank")
@Getter
@Setter
public class BankProperties {
    private String bankCode;
    private String cityCode;
    private String currency;
    private String accountNumberInitialValue;

}
