package com.ebank.account.Commands.command;

import com.ebank.account.Common.enums.AccountStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class SuspendAccountCommand extends BaseCommand<UUID> {
    private final AccountStatus accountStatus;

    public SuspendAccountCommand(UUID id, LocalDateTime commandTimestamp, AccountStatus accountStatus) {
        super(id, commandTimestamp);
        this.accountStatus = AccountStatus.SUSPENDED;
    }
}
