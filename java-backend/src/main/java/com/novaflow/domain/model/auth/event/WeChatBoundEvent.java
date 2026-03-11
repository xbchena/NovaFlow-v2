package com.novaflow.domain.model.auth.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 微信账号绑定事件
 */
@Getter
public class WeChatBoundEvent extends DomainEvent {

    private final String userId;
    private final String openid;

    public WeChatBoundEvent(String userId, String openid) {
        super();
        this.userId = userId;
        this.openid = openid;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }
}
