package com.novaflow.interfaces.dto.response;

import java.util.List;

/**
 * 推荐历史响应DTO
 */
public class RecommendationHistoryResponse {

    private List<RecommendationItem> recommendations;
    private Integer total;

    // 无参构造函数
    public RecommendationHistoryResponse() {
    }

    // 全参构造函数
    public RecommendationHistoryResponse(List<RecommendationItem> recommendations, Integer total) {
        this.recommendations = recommendations;
        this.total = total;
    }

    // Getters
    public List<RecommendationItem> getRecommendations() {
        return recommendations;
    }

    public Integer getTotal() {
        return total;
    }

    // Setters
    public void setRecommendations(List<RecommendationItem> recommendations) {
        this.recommendations = recommendations;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 推荐历史项
     */
    public static class RecommendationItem {
        private String id;
        private String sceneType;
        private String sceneDescription;
        private List<String> recommendations;
        private String createdAt;

        // 无参构造函数
        public RecommendationItem() {
        }

        // 全参构造函数
        public RecommendationItem(String id, String sceneType, String sceneDescription,
                                 List<String> recommendations, String createdAt) {
            this.id = id;
            this.sceneType = sceneType;
            this.sceneDescription = sceneDescription;
            this.recommendations = recommendations;
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

        public List<String> getRecommendations() {
            return recommendations;
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

        public void setRecommendations(List<String> recommendations) {
            this.recommendations = recommendations;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
