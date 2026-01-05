package com.ebank.account.Commands.command;

import com.ebank.account.Common.enums.AccountStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ActivateAccountCommand extends BaseCommand<UUID> {
    private final AccountStatus accountStatus;
    public ActivateAccountCommand(UUID id, LocalDateTime commandTimestamp, AccountStatus accountStatus) {
        super(id, commandTimestamp);
        this.accountStatus = AccountStatus.ACTIVATED;
    }
}
