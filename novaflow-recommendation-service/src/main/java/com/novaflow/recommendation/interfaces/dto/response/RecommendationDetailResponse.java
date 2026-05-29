package com.novaflow.recommendation.interfaces.dto.response;

import java.util.List;

/**
 * 推荐详情响应DTO
 */
public class RecommendationDetailResponse {

    private String id;
    private String sceneType;
    private String sceneDescription;
    private List<FoodRecommendation> recommendations;
    private List<NearbyPlace> nearbyPlaces;
    private String createdAt;

    // 无参构造函数
    public RecommendationDetailResponse() {
    }

    // 全参构造函数
    public RecommendationDetailResponse(String id, String sceneType, String sceneDescription,
                                       List<FoodRecommendation> recommendations,
                                       List<NearbyPlace> nearbyPlaces, String createdAt) {
        this.id = id;
        this.sceneType = sceneType;
        this.sceneDescription = sceneDescription;
        this.recommendations = recommendations;
        this.nearbyPlaces = nearbyPlaces;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getSceneType() {
        return sceneType;
    }

    public String getSceneDescription() {
        return sceneDescription;
    }

    public List<FoodRecommendation> getRecommendations() {
        return recommendations;
    }

    public List<NearbyPlace> getNearbyPlaces() {
        return nearbyPlaces;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public void setSceneDescription(String sceneDescription) {
        this.sceneDescription = sceneDescription;
    }

    public void setRecommendations(List<FoodRecommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public void setNearbyPlaces(List<NearbyPlace> nearbyPlaces) {
        this.nearbyPlaces = nearbyPlaces;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 美食推荐项
     */
    public static class FoodRecommendation {
        private String foodName;
        private String foodType;
        private String reason;
        private String priceHint;
        private Integer matchScore;

        // 无参构造函数
        public FoodRecommendation() {
        }

        // 全参构造函数
        public FoodRecommendation(String foodName, String foodType, String reason,
                                  String priceHint, Integer matchScore) {
            this.foodName = foodName;
            this.foodType = foodType;
            this.reason = reason;
            this.priceHint = priceHint;
            this.matchScore = matchScore;
        }

        // Getters
        public String getFoodName() {
            return foodName;
        }

        public String getFoodType() {
            return foodType;
        }

        public String getReason() {
            return reason;
        }

        public String getPriceHint() {
            return priceHint;
        }

        public Integer getMatchScore() {
            return matchScore;
        }

        // Setters
        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public void setFoodType(String foodType) {
            this.foodType = foodType;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public void setPriceHint(String priceHint) {
            this.priceHint = priceHint;
        }

        public void setMatchScore(Integer matchScore) {
            this.matchScore = matchScore;
        }
    }

    /**
     * 附近地点
     */
    public static class NearbyPlace {
        private String id;
        private String name;
        private String address;
        private Integer distance;
        private String category;

        // 无参构造函数
        public NearbyPlace() {
        }

        // 全参构造函数
        public NearbyPlace(String id, String name, String address,
                          Integer distance, String category) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.distance = distance;
            this.category = category;
        }

        // Getters
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public Integer getDistance() {
            return distance;
        }

        public String getCategory() {
            return category;
        }

        // Setters
        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
