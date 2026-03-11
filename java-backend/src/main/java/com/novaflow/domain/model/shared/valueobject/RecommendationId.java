package com.novaflow.domain.model.shared.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 推荐标识值对象
 * 表示系统中唯一的推荐标识
 */
@Getter
@EqualsAndHashCode
public class RecommendationId {

    private final String value;

    private RecommendationId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("推荐ID不能为空");
        }
        this.value = value;
    }

    /**
     * 从现有字符串创建RecommendationId
     */
    public static RecommendationId of(String value) {
        return new RecommendationId(value);
    }

    /**
     * 生成新的RecommendationId
     */
    public static RecommendationId generate() {
        return new RecommendationId(UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * 从UUID创建RecommendationId
     */
    public static RecommendationId fromUUID(UUID uuid) {
        return new RecommendationId(uuid.toString().replace("-", ""));
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * 获取值
     */
    public String getValue() {
        return value;
    }
}
