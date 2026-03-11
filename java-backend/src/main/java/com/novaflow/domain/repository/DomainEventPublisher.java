package com.novaflow.domain.repository;

import com.novaflow.domain.event.DomainEvent;

/**
 * 领域事件发布器接口
 * 用于发布领域事件
 */
public interface DomainEventPublisher {

    /**
     * 发布领域事件
     */
    void publish(DomainEvent event);

    /**
     * 异步发布领域事件
     */
    void publishAsync(DomainEvent event);
}
