package com.novaflow.domain.model.auth.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 手机号绑定事件
 */
@Getter
public class PhoneBoundEvent extends DomainEvent {

    private final String userId;
    private final String phone;

    public PhoneBoundEvent(String userId, String phone) {
        super();
        this.userId = userId;
        this.phone = phone;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }
}
