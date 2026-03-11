package com.novaflow.domain.model.auth.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 用户创建事件
 */
@Getter
public class UserCreatedEvent extends DomainEvent {

    private final String userId;
    private final String identifier; // openid or phone

    public UserCreatedEvent(String userId, String identifier) {
        super();
        this.userId = userId;
        this.identifier = identifier;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }
}
