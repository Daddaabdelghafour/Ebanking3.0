package com.ebank.account.Commands.command;

import com.ebank.account.Common.enums.AccountStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CreateAccountCommand extends BaseCommand<UUID>{
    private final UUID customerId;
    private final String email;
    private final BigDecimal balance;
    private final AccountStatus accountStatus;

    public CreateAccountCommand(UUID id, LocalDateTime commandTimestamp, UUID customerId, String email, BigDecimal balance, AccountStatus accountStatus) {
        super(id, commandTimestamp);
        this.customerId = customerId;
        this.email = email;
        this.balance = balance;
        this.accountStatus = accountStatus;
    }
}
