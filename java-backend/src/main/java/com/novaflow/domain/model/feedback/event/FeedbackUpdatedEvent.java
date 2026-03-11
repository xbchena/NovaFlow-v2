package com.novaflow.domain.model.feedback.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 反馈更新事件
 */
@Getter
public class FeedbackUpdatedEvent extends DomainEvent {

    private final String selectionId;
    private final String userId;
    private final String oldFeedbackType;
    private final String newFeedbackType;

    public FeedbackUpdatedEvent(String selectionId, String userId, String oldFeedbackType, String newFeedbackType) {
        super();
        this.selectionId = selectionId;
        this.userId = userId;
        this.oldFeedbackType = oldFeedbackType;
        this.newFeedbackType = newFeedbackType;
    }

    @Override
    public String getAggregateId() {
        return selectionId;
    }
}
