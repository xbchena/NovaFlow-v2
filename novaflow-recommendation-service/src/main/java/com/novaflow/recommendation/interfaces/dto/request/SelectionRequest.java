package com.novaflow.recommendation.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 选择请求DTO
 */
public class SelectionRequest {

    /**
     * 推荐ID
     */
    @NotBlank(message = "推荐ID不能为空")
    private String recommendationId;

    /**
     * 选择的食物
     */
    @NotBlank(message = "选择的食物不能为空")
    private String selectedFood;

    /**
     * 选择的地点
     */
    private String selectedPlace;

    /**
     * 反馈类型：positive, neutral, negative
     */
    private String feedback;

    // Getters
    public String getRecommendationId() {
        return recommendationId;
    }

    public String getSelectedFood() {
        return selectedFood;
    }

    public String getSelectedPlace() {
        return selectedPlace;
    }

    public String getFeedback() {
        return feedback;
    }

    // Setters
    public void setRecommendationId(String recommendationId) {
        this.recommendationId = recommendationId;
    }

    public void setSelectedFood(String selectedFood) {
        this.selectedFood = selectedFood;
    }

    public void setSelectedPlace(String selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
