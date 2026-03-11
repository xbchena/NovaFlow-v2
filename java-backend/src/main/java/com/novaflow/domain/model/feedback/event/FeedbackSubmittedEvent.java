package com.novaflow.domain.model.feedback.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 反馈提交事件
 */
@Getter
public class FeedbackSubmittedEvent extends DomainEvent {

    private final String selectionId;
    private final String userId;
    private final String feedbackType;

    public FeedbackSubmittedEvent(String selectionId, String userId, String feedbackType) {
        super();
        this.selectionId = selectionId;
        this.userId = userId;
        this.feedbackType = feedbackType;
    }

    @Override
    public String getAggregateId() {
        return selectionId;
    }
}
