package com.novaflow.common.domain.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {

    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String eventType;

    protected DomainEvent() {
        this.eventType = this.getClass().getSimpleName();
    }

    public abstract String getAggregateId();

    public String getEventType() {
        return eventType;
    }
}
