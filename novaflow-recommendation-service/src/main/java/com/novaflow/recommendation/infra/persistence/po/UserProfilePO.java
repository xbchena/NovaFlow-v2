package com.novaflow.recommendation.infra.persistence.po;

import java.time.LocalDateTime;

/**
 * 用户画像持久化对象
 * 对应数据库表 user_profile
 */
public class UserProfilePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 饮食偏好 (JSON)
     */
    private String dietPreferences;

    /**
     * 过敏信息 (JSON)
     */
    private String allergies;

    /**
     * 喜好菜系 (JSON)
     */
    private String preferredCuisines;

    /**
     * 价格偏好
     */
    private String preferredPriceRange;

    /**
     * 行为摘要 (JSON)
     */
    private String behaviorSummary;

    /**
     * 累计推荐次数
     */
    private Integer totalRecommendations;

    /**
     * 累计点击次数
     */
    private Integer totalClicks;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // Getters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDietPreferences() {
        return dietPreferences;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getPreferredCuisines() {
        return preferredCuisines;
    }

    public String getPreferredPriceRange() {
        return preferredPriceRange;
    }

    public String getBehaviorSummary() {
        return behaviorSummary;
    }

    public Integer getTotalRecommendations() {
        return totalRecommendations;
    }

    public Integer getTotalClicks() {
        return totalClicks;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDietPreferences(String dietPreferences) {
        this.dietPreferences = dietPreferences;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public void setPreferredCuisines(String preferredCuisines) {
        this.preferredCuisines = preferredCuisines;
    }

    public void setPreferredPriceRange(String preferredPriceRange) {
        this.preferredPriceRange = preferredPriceRange;
    }

    public void setBehaviorSummary(String behaviorSummary) {
        this.behaviorSummary = behaviorSummary;
    }

    public void setTotalRecommendations(Integer totalRecommendations) {
        this.totalRecommendations = totalRecommendations;
    }

    public void setTotalClicks(Integer totalClicks) {
        this.totalClicks = totalClicks;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
