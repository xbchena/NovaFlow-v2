package com.novaflow.recommendation.domain.model.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户画像领域模型
 * 包含用户的饮食偏好、过敏信息、行为摘要等长期记忆
 */
@Getter
@EqualsAndHashCode
public class UserProfile {

    private final Long userId;
    private List<String> dietPreferences;
    private List<String> allergies;
    private List<String> preferredCuisines;
    private String preferredPriceRange;
    private Map<String, Object> behaviorSummary;
    private int totalRecommendations;
    private int totalClicks;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private UserProfile(Long userId, List<String> dietPreferences, List<String> allergies,
                        List<String> preferredCuisines, String preferredPriceRange,
                        Map<String, Object> behaviorSummary,
                        int totalRecommendations, int totalClicks,
                        LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.userId = userId;
        this.dietPreferences = dietPreferences != null ? dietPreferences : List.of();
        this.allergies = allergies != null ? allergies : List.of();
        this.preferredCuisines = preferredCuisines != null ? preferredCuisines : List.of();
        this.preferredPriceRange = preferredPriceRange;
        this.behaviorSummary = behaviorSummary != null ? behaviorSummary : Map.of();
        this.totalRecommendations = totalRecommendations;
        this.totalClicks = totalClicks;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static UserProfile create(Long userId) {
        return new UserProfile(userId, List.of(), List.of(), List.of(), null,
                Map.of(), 0, 0, LocalDateTime.now(), LocalDateTime.now());
    }

    public static UserProfile reconstruct(Long userId, List<String> dietPreferences,
                                           List<String> allergies, List<String> preferredCuisines,
                                           String preferredPriceRange,
                                           Map<String, Object> behaviorSummary,
                                           int totalRecommendations, int totalClicks,
                                           LocalDateTime updatedAt, LocalDateTime createdAt) {
        return new UserProfile(userId, dietPreferences, allergies, preferredCuisines,
                preferredPriceRange, behaviorSummary,
                totalRecommendations, totalClicks, updatedAt, createdAt);
    }

    public UserProfile updatePreferences(List<String> newPreferences) {
        return new UserProfile(this.userId, newPreferences, this.allergies,
                this.preferredCuisines, this.preferredPriceRange,
                this.behaviorSummary, this.totalRecommendations, this.totalClicks,
                LocalDateTime.now(), this.createdAt);
    }

    public UserProfile updateAllergies(List<String> newAllergies) {
        return new UserProfile(this.userId, this.dietPreferences, newAllergies,
                this.preferredCuisines, this.preferredPriceRange,
                this.behaviorSummary, this.totalRecommendations, this.totalClicks,
                LocalDateTime.now(), this.createdAt);
    }

    public UserProfile incrementRecommendations(int count) {
        return new UserProfile(this.userId, this.dietPreferences, this.allergies,
                this.preferredCuisines, this.preferredPriceRange,
                this.behaviorSummary, this.totalRecommendations + count, this.totalClicks,
                LocalDateTime.now(), this.createdAt);
    }

    public UserProfile incrementClicks(int count) {
        return new UserProfile(this.userId, this.dietPreferences, this.allergies,
                this.preferredCuisines, this.preferredPriceRange,
                this.behaviorSummary, this.totalRecommendations, this.totalClicks + count,
                LocalDateTime.now(), this.createdAt);
    }

    public UserProfile updateBehaviorSummary(Map<String, Object> summary) {
        return new UserProfile(this.userId, this.dietPreferences, this.allergies,
                this.preferredCuisines, this.preferredPriceRange,
                summary, this.totalRecommendations, this.totalClicks,
                LocalDateTime.now(), this.createdAt);
    }

    /**
     * 生成用于注入 system prompt 的画像文本
     */
    public String toPromptText() {
        StringBuilder sb = new StringBuilder();
        sb.append("## 用户画像\n");
        if (!dietPreferences.isEmpty()) {
            sb.append("- 饮食偏好: ").append(String.join("、", dietPreferences)).append("\n");
        }
        if (!allergies.isEmpty()) {
            sb.append("- 过敏信息: ").append(String.join("、", allergies)).append("\n");
        }
        if (!preferredCuisines.isEmpty()) {
            sb.append("- 喜好菜系: ").append(String.join("、", preferredCuisines)).append("\n");
        }
        if (preferredPriceRange != null) {
            sb.append("- 价格偏好: ").append(preferredPriceRange).append("\n");
        }
        sb.append("- 累计推荐: ").append(totalRecommendations).append("次, 点击: ").append(totalClicks).append("次\n");
        return sb.toString();
    }

    public boolean isNewUser() {
        return dietPreferences.isEmpty() && allergies.isEmpty()
                && preferredCuisines.isEmpty() && totalRecommendations == 0;
    }
}
