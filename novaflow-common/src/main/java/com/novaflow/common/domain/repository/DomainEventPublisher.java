package com.novaflow.common.domain.repository;

import com.novaflow.common.domain.event.DomainEvent;

public interface DomainEventPublisher {

    void publish(DomainEvent event);

    void publishAsync(DomainEvent event);
}
