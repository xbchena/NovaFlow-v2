package com.novaflow.domain.model.feedback.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 选择删除事件
 */
@Getter
public class SelectionDeletedEvent extends DomainEvent {

    private final String selectionId;
    private final String userId;

    public SelectionDeletedEvent(String selectionId, String userId) {
        super();
        this.selectionId = selectionId;
        this.userId = userId;
    }

    @Override
    public String getAggregateId() {
        return selectionId;
    }
}
