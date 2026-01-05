package com.ebank.account.Common.event;

import com.ebank.account.Common.enums.OperationType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AccountDebitedEvent extends BaseEvent<UUID> {
    private final BigDecimal amount;
    private final OperationType operationType;
    private final String description;

    public AccountDebitedEvent(UUID id, LocalDateTime eventTimestamp, BigDecimal amount, String description) {
        super(id, eventTimestamp);
        this.amount = amount;
        this.operationType = OperationType.DEBIT;
        this.description = description;
    }

}
