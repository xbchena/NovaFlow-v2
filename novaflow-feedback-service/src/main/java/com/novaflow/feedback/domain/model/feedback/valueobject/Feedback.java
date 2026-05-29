package com.novaflow.feedback.domain.model.feedback.valueobject;

import com.novaflow.common.domain.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 反馈值对象
 * 表示用户对推荐的反馈意见
 */
@Getter
@EqualsAndHashCode
public class Feedback implements ValueObject {

    private final FeedbackType type;
    private final String comment;
    private final Integer rating; // 1-5分

    private Feedback(FeedbackType type, String comment, Integer rating) {
        if (type == null) {
            throw new IllegalArgumentException("反馈类型不能为空");
        }
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }

        this.type = type;
        this.comment = comment;
        this.rating = rating;
    }

    /**
     * 创建积极反馈
     */
    public static Feedback positive(String comment, Integer rating) {
        return new Feedback(FeedbackType.POSITIVE, comment, rating);
    }

    /**
     * 创建中性反馈
     */
    public static Feedback neutral(String comment) {
        return new Feedback(FeedbackType.NEUTRAL, comment, null);
    }

    /**
     * 创建消极反馈
     */
    public static Feedback negative(String comment, Integer rating) {
        return new Feedback(FeedbackType.NEGATIVE, comment, rating);
    }

    /**
     * 创建不带评论的反馈
     */
    public static Feedback of(FeedbackType type) {
        return new Feedback(type, null, null);
    }

    /**
     * 检查是否有评论
     */
    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }

    /**
     * 检查是否有评分
     */
    public boolean hasRating() {
        return rating != null;
    }

    /**
     * 检查是否为积极反馈
     */
    public boolean isPositive() {
        return type == FeedbackType.POSITIVE;
    }

    /**
     * 检查是否为消极反馈
     */
    public boolean isNegative() {
        return type == FeedbackType.NEGATIVE;
    }

    /**
     * 获取反馈类型
     */
    public FeedbackType type() {
        return type;
    }

    /**
     * 获取评论
     */
    public String comment() {
        return comment;
    }

    /**
     * 获取评分
     */
    public Integer rating() {
        return rating;
    }

    /**
     * 获取反馈类型 (Lombok 风格)
     */
    public FeedbackType getType() {
        return type;
    }

    /**
     * 反馈类型枚举
     */
    public enum FeedbackType {
        POSITIVE,  // 积极
        NEUTRAL,   // 中性
        NEGATIVE   // 消极
    }
}
