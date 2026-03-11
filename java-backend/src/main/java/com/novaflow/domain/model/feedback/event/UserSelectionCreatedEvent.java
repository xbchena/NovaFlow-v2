package com.novaflow.domain.model.feedback.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 用户选择创建事件
 */
@Getter
public class UserSelectionCreatedEvent extends DomainEvent {

    private final String selectionId;
    private final String userId;
    private final String recommendationId;
    private final String foodName;

    public UserSelectionCreatedEvent(String selectionId, String userId, String recommendationId, String foodName) {
        super();
        this.selectionId = selectionId;
        this.userId = userId;
        this.recommendationId = recommendationId;
        this.foodName = foodName;
    }

    @Override
    public String getAggregateId() {
        return selectionId;
    }
}
