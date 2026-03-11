package com.novaflow.domain.model.auth.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 用户删除事件
 */
@Getter
public class UserDeletedEvent extends DomainEvent {

    private final String userId;

    public UserDeletedEvent(String userId) {
        super();
        this.userId = userId;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }
}
