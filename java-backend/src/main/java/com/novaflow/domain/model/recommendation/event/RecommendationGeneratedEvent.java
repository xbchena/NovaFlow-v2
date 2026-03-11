package com.novaflow.domain.model.recommendation.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 推荐生成事件
 */
@Getter
public class RecommendationGeneratedEvent extends DomainEvent {

    private final String recommendationId;
    private final String userId;
    private final String videoId;
    private final int itemCount;

    public RecommendationGeneratedEvent(String recommendationId, String userId, String videoId, int itemCount) {
        super();
        this.recommendationId = recommendationId;
        this.userId = userId;
        this.videoId = videoId;
        this.itemCount = itemCount;
    }

    @Override
    public String getAggregateId() {
        return recommendationId;
    }
}
