package com.ebank.account.Common.event;

import com.ebank.account.Common.enums.AccountStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AccountActivatedEvent extends BaseEvent<UUID> {
    private final AccountStatus accountStatus;

    public AccountActivatedEvent(UUID id, LocalDateTime eventDate) {
        super(id, eventDate);
        this.accountStatus = AccountStatus.ACTIVATED;
    }
}
