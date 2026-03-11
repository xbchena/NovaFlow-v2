package com.novaflow.domain.model.shared.aggregate;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类
 * 所有聚合根必须继承此类以获得领域事件发布能力
 */
@Getter
public abstract class AggregateRoot {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 添加领域事件
     */
    protected void addDomainEvent(DomainEvent event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }

    /**
     * 获取所有领域事件（不可修改列表）
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * 清除已发布的领域事件
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    /**
     * 获取聚合根ID
     */
    public abstract String getId();
}
