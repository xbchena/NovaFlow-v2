package com.novaflow.common.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

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

    public static RecommendationId of(String value) {
        return new RecommendationId(value);
    }

    public static RecommendationId generate() {
        return new RecommendationId(UUID.randomUUID().toString().replace("-", ""));
    }

    public static RecommendationId fromUUID(UUID uuid) {
        return new RecommendationId(uuid.toString().replace("-", ""));
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }
}
