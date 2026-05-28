package com.novaflow.common.infra.event;

import com.novaflow.common.domain.event.DomainEvent;
import com.novaflow.common.domain.repository.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SpringDomainEventPublisher.class);

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        if (event == null) {
            log.warn("尝试发布空的事件");
            return;
        }
        try {
            log.debug("发布领域事件: {} (AggregateId: {})", event.getEventType(), event.getAggregateId());
            applicationEventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("发布领域事件失败: {}", event.getEventType(), e);
            throw new EventPublishException("领域事件发布失败", e);
        }
    }

    @Override
    @Async
    public void publishAsync(DomainEvent event) {
        publish(event);
    }

    public static class EventPublishException extends RuntimeException {
        public EventPublishException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
