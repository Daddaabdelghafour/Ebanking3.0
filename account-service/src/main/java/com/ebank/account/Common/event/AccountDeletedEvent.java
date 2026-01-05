package com.ebank.account.Common.event;

import com.ebank.account.Common.enums.AccountStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AccountDeletedEvent extends BaseEvent<UUID>{
    private final AccountStatus accountStatus;

    public AccountDeletedEvent(UUID id, LocalDateTime eventTimestamp) {
        super(id, eventTimestamp);
        this.accountStatus = AccountStatus.DELETED;
    }
}
