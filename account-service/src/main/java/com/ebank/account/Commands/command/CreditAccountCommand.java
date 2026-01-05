package com.ebank.account.Commands.command;

import com.ebank.account.Common.enums.OperationType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CreditAccountCommand extends BaseCommand<UUID>{
    private final BigDecimal amount;
    private final OperationType operationType;
    private final String description;

    public CreditAccountCommand(
            UUID id,
            LocalDateTime commandTimestamp,
            BigDecimal amount,
            String description
    ){
        super(id, commandTimestamp);
        this.amount = amount;
        this.operationType = OperationType.CREDIT;
        this.description = description;
    }
}
