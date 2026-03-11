package com.novaflow.domain.model.feedback.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 选择已使用事件
 */
@Getter
public class SelectionUsedEvent extends DomainEvent {

    private final String selectionId;
    private final String userId;
    private final String foodName;

    public SelectionUsedEvent(String selectionId, String userId, String foodName) {
        super();
        this.selectionId = selectionId;
        this.userId = userId;
        this.foodName = foodName;
    }

    @Override
    public String getAggregateId() {
        return selectionId;
    }
}
