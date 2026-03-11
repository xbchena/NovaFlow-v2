package com.novaflow.infrastructure.messaging.event;

import com.novaflow.domain.event.DomainEvent;
import com.novaflow.domain.repository.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 基于Spring的领域事件发布器实现
 * 使用Spring的ApplicationEventPublisher发布领域事件
 */
@Slf4j
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

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

    /**
     * 事件发布异常
     */
    public static class EventPublishException extends RuntimeException {
        public EventPublishException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
