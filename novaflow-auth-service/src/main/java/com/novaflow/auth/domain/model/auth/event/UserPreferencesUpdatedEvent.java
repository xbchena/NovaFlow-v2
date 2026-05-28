package com.novaflow.auth.domain.model.auth.event;

import com.novaflow.common.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 用户偏好更新事件
 */
@Getter
public class UserPreferencesUpdatedEvent extends DomainEvent {

    private final String userId;

    public UserPreferencesUpdatedEvent(String userId) {
        super();
        this.userId = userId;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }
}
