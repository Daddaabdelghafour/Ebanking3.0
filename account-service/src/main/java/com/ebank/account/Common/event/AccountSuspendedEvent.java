package com.ebank.account.Common.event;

import lombok.Getter;
import com.ebank.account.Common.enums.AccountStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AccountSuspendedEvent extends BaseEvent<UUID> {
    private final AccountStatus accountStatus;

    public AccountSuspendedEvent(UUID id, LocalDateTime eventTimestamp) {
        super(id, eventTimestamp);
        this.accountStatus = AccountStatus.SUSPENDED;
    }
}