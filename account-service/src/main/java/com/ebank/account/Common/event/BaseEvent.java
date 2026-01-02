package com.ebank.account.Common.event;

import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Getter
public class BaseEvent<T> {
    @TargetAggregateIdentifier
    private final T id;
    private final LocalDateTime eventTimestamp;

    public BaseEvent(T id, LocalDateTime eventTimestamp) {
        this.id = id;
        this.eventTimestamp = eventTimestamp;
    }
}