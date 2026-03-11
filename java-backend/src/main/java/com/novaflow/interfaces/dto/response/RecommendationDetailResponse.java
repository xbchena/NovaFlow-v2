package com.novaflow.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 推荐详情响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDetailResponse {

    private String id;
    private String sceneType;
    private String sceneDescription;
    private List<FoodRecommendation> recommendations;
    private List<NearbyPlace> nearbyPlaces;
    private String createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodRecommendation {
        private String foodName;
        private String foodType;
        private String reason;
        private String priceHint;
        private Integer matchScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NearbyPlace {
        private String id;
        private String name;
        private String address;
        private Integer distance;
        private String category;
    }
}
