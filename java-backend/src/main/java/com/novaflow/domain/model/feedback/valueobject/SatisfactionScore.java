package com.novaflow.domain.model.feedback.valueobject;

import com.novaflow.domain.model.shared.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 满意度分数值对象
 * 表示用户对推荐的整体满意度（1-100分）
 */
@Getter
@EqualsAndHashCode
public class SatisfactionScore implements ValueObject {

    private final Integer score;

    private SatisfactionScore(Integer score) {
        if (score == null || score < 1 || score > 100) {
            throw new IllegalArgumentException("满意度分数必须在1-100之间");
        }
        this.score = score;
    }

    /**
     * 创建满意度分数
     */
    public static SatisfactionScore of(Integer score) {
        return new SatisfactionScore(score);
    }

    /**
     * 创建高满意度（>=80分）
     */
    public static SatisfactionScore high() {
        return new SatisfactionScore(85);
    }

    /**
     * 创建中等满意度（50-79分）
     */
    public static SatisfactionScore medium() {
        return new SatisfactionScore(65);
    }

    /**
     * 创建低满意度（<50分）
     */
    public static SatisfactionScore low() {
        return new SatisfactionScore(35);
    }

    /**
     * 检查是否为高满意度
     */
    public boolean isHigh() {
        return score >= 80;
    }

    /**
     * 检查是否为中等满意度
     */
    public boolean isMedium() {
        return score >= 50 && score < 80;
    }

    /**
     * 检查是否为低满意度
     */
    public boolean isLow() {
        return score < 50;
    }

    /**
     * 获取满意度等级
     */
    public SatisfactionLevel getLevel() {
        if (isHigh()) {
            return SatisfactionLevel.HIGH;
        } else if (isMedium()) {
            return SatisfactionLevel.MEDIUM;
        } else {
            return SatisfactionLevel.LOW;
        }
    }

    /**
     * 满意度等级
     */
    public enum SatisfactionLevel {
        HIGH,    // 高
        MEDIUM,  // 中
        LOW      // 低
    }
}
