package com.ebank.account.Commands.command;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BaseCommand<T> {
    private final T id;
    private final LocalDateTime commandTimestamp;

    public BaseCommand(T id, LocalDateTime commandTimestamp) {
        this.id = id;
        this.commandTimestamp = commandTimestamp;
    }
}
