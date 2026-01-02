package com.ebank.account.Common.event;

import com.ebank.account.Common.enums.AccountStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AccountCreationEvent extends BaseEvent<UUID>{

    private final UUID customerId;
    private final String email;
    private final BigDecimal balance;
    private final AccountStatus accountStatus;

    public AccountCreationEvent(UUID id, LocalDateTime eventTimestamp, UUID customerId, String email, BigDecimal balance) {
        super(id, eventTimestamp);
        this.customerId = customerId;
        this.email = email;
        this.balance = balance;
        this.accountStatus = AccountStatus.CREATED;
    }

}
