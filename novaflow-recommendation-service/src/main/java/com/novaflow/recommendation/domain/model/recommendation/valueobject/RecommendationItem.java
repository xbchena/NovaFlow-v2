package com.novaflow.recommendation.domain.model.recommendation.valueobject;

import com.novaflow.common.domain.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 推荐项值对象
 * 表示单个食物推荐
 */
@Getter
@EqualsAndHashCode
public class RecommendationItem implements ValueObject {

    private final String foodName;
    private final String foodType;
    private final String reason;
    private final String priceHint;
    private final Integer matchScore;
    private final String imageUrl;

    private RecommendationItem(String foodName, String foodType, String reason,
                              String priceHint, Integer matchScore, String imageUrl) {
        if (foodName == null || foodName.trim().isEmpty()) {
            throw new IllegalArgumentException("食物名称不能为空");
        }
        if (matchScore != null && (matchScore < 0 || matchScore > 100)) {
            throw new IllegalArgumentException("匹配分数必须在0-100之间");
        }

        this.foodName = foodName;
        this.foodType = foodType;
        this.reason = reason;
        this.priceHint = priceHint;
        this.matchScore = matchScore != null ? matchScore : 0;
        this.imageUrl = imageUrl;
    }

    // Explicit getters for Lombok compatibility
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

    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 创建推荐项
     */
    public static RecommendationItem of(String foodName, String foodType, String reason,
                                       String priceHint, Integer matchScore) {
        return new RecommendationItem(foodName, foodType, reason, priceHint, matchScore, null);
    }

    /**
     * 创建带图片的推荐项
     */
    public static RecommendationItem of(String foodName, String foodType, String reason,
                                       String priceHint, Integer matchScore, String imageUrl) {
        return new RecommendationItem(foodName, foodType, reason, priceHint, matchScore, imageUrl);
    }

    /**
     * 创建最小信息的推荐项
     */
    public static RecommendationItem of(String foodName, String foodType) {
        return new RecommendationItem(foodName, foodType, null, null, null, null);
    }

    /**
     * 检查是否有图片
     */
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }

    /**
     * 检查是否为高匹配度（>=70分）
     */
    public boolean isHighMatch() {
        return matchScore >= 70;
    }

    /**
     * 检查是否为中等匹配度（40-69分）
     */
    public boolean isMediumMatch() {
        return matchScore >= 40 && matchScore < 70;
    }

    /**
     * 检查是否有推荐理由
     */
    public boolean hasReason() {
        return reason != null && !reason.trim().isEmpty();
    }

    /**
     * 检查是否有价格提示
     */
    public boolean hasPriceHint() {
        return priceHint != null && !priceHint.trim().isEmpty();
    }
}
