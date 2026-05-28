package com.novaflow.auth.domain.model.auth.event;

import com.novaflow.common.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 手机号更换事件
 */
@Getter
public class PhoneChangedEvent extends DomainEvent {

    private final String userId;
    private final String oldPhone;
    private final String newPhone;

    public PhoneChangedEvent(String userId, String oldPhone, String newPhone) {
        super();
        this.userId = userId;
        this.oldPhone = oldPhone;
        this.newPhone = newPhone;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }
}
