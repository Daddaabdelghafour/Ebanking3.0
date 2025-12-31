package com.ebank.account.Common.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BaseEvent<T> {
    private final T id;
    private final LocalDateTime eventTimestamp;

    public BaseEvent(T id, LocalDateTime eventTimestamp) {
        this.id = id;
        this.eventTimestamp = eventTimestamp;
    }
}

