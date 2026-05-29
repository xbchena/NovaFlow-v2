package com.novaflow.recommendation.domain.model.recommendation.event;

import com.novaflow.common.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 推荐删除事件
 */
@Getter
public class RecommendationDeletedEvent extends DomainEvent {

    private final String recommendationId;
    private final String userId;

    public RecommendationDeletedEvent(String recommendationId, String userId) {
        super();
        this.recommendationId = recommendationId;
        this.userId = userId;
    }

    @Override
    public String getAggregateId() {
        return recommendationId;
    }
}
