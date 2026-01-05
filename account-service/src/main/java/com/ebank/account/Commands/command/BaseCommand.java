package com.ebank.account.Commands.command;

import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Getter
public class BaseCommand<T> {
    @TargetAggregateIdentifier
    private final T id;
    private final LocalDateTime commandTimestamp;

    public BaseCommand(T id, LocalDateTime commandTimestamp) {
        this.id = id;
        this.commandTimestamp = commandTimestamp;
    }
}