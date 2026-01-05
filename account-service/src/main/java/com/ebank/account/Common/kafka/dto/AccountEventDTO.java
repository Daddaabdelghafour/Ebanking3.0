package com.ebank.account.Common.kafka.dto;

import com.ebank.account.Common.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEventDTO {
    private UUID accountId;
    private UUID customerId;
    private String email;
    private String accountNumber;
    private String iban;
    private BigDecimal balance;
    private AccountStatus status;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private String eventType;
}
