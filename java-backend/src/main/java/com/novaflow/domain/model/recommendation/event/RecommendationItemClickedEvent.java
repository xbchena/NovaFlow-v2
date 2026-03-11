package com.novaflow.domain.model.recommendation.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 推荐项点击事件
 */
@Getter
public class RecommendationItemClickedEvent extends DomainEvent {

    private final String recommendationId;
    private final String userId;
    private final String foodName;
    private final String placeId;

    public RecommendationItemClickedEvent(String recommendationId, String userId, String foodName, String placeId) {
        super();
        this.recommendationId = recommendationId;
        this.userId = userId;
        this.foodName = foodName;
        this.placeId = placeId;
    }

    @Override
    public String getAggregateId() {
        return recommendationId;
    }
}
