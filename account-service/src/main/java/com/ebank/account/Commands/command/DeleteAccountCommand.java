package com.ebank.account.Commands.command;

import com.ebank.account.Common.enums.AccountStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class DeleteAccountCommand extends BaseCommand<UUID>{
    private final AccountStatus accountStatus;

    public DeleteAccountCommand(UUID id, LocalDateTime commandTimestamp) {
        super(id, commandTimestamp);
        this.accountStatus = AccountStatus.DELETED;
    }
}
