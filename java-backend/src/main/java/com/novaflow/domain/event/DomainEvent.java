package com.novaflow.domain.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * 领域事件接口
 * 所有领域事件必须实现此接口
 */
@Getter
public abstract class DomainEvent {

    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredAt = Instant.now();
    private final String eventType;

    protected DomainEvent() {
        this.eventType = this.getClass().getSimpleName();
    }

    /**
     * 获取聚合根ID，用于事件溯源和关联
     */
    public abstract String getAggregateId();

    /**
     * 获取事件类型
     */
    public String getEventType() {
        return eventType;
    }
}
