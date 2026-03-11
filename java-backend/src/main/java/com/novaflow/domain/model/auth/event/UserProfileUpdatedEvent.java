package com.novaflow.domain.model.auth.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 用户信息更新事件
 */
@Getter
public class UserProfileUpdatedEvent extends DomainEvent {

    private final String userId;

    public UserProfileUpdatedEvent(String userId) {
        super();
        this.userId = userId;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }
}
