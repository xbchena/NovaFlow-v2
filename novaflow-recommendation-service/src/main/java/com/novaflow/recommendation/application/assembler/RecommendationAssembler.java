package com.novaflow.recommendation.application.assembler;

import com.novaflow.recommendation.domain.model.recommendation.Recommendation;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.RecommendationItem;
import com.novaflow.recommendation.interfaces.dto.response.RecommendationDetailResponse;
import com.novaflow.recommendation.interfaces.dto.response.RecommendationHistoryResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 推荐聚合转换器
 * 负责领域对象与DTO之间的转换
 */
public class RecommendationAssembler {

    /**
     * 转换为推荐历史响应
     */
    public static RecommendationHistoryResponse toHistoryResponse(List<Recommendation> recommendations, long total) {
        List<RecommendationHistoryResponse.RecommendationItem> items = recommendations.stream()
                .map(RecommendationAssembler::toHistoryItem)
                .collect(Collectors.toList());

        return new RecommendationHistoryResponse(items, (int) total);
    }

    /**
     * 转换为历史记录项
     */
    private static RecommendationHistoryResponse.RecommendationItem toHistoryItem(Recommendation recommendation) {
        return new RecommendationHistoryResponse.RecommendationItem(
                recommendation.getRecommendationId().getValue(),
                recommendation.getSceneAnalysis().getSceneType(),
                recommendation.getSceneAnalysis().getSceneDescription(),
                recommendation.getItems().stream()
                        .map(RecommendationItem::getFoodName)
                        .collect(Collectors.toList()),
                recommendation.getCreatedAt().toString()
        );
    }

    /**
     * 转换为推荐详情响应
     */
    public static RecommendationDetailResponse toDetailResponse(Recommendation recommendation) {
        List<RecommendationDetailResponse.FoodRecommendation> foodRecommendations = recommendation.getItems().stream()
                .map(item -> new RecommendationDetailResponse.FoodRecommendation(
                        item.getFoodName(),
                        item.getFoodType(),
                        item.getReason(),
                        item.getPriceHint(),
                        item.getMatchScore()
                ))
                .collect(Collectors.toList());

        return new RecommendationDetailResponse(
                recommendation.getRecommendationId().getValue(),
                recommendation.getSceneAnalysis().getSceneType(),
                recommendation.getSceneAnalysis().getSceneDescription(),
                foodRecommendations,
                List.of(), // 附近地点信息可从地图服务获取
                recommendation.getCreatedAt().toString()
        );
    }
}
