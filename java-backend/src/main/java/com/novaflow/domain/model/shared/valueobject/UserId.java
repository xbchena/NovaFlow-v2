package com.novaflow.domain.model.shared.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 用户标识值对象
 * 表示系统中唯一的用户标识
 */
@Getter
@EqualsAndHashCode
public class UserId {

    private final String value;

    private UserId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        this.value = value;
    }

    /**
     * 从现有字符串创建UserId
     */
    public static UserId of(String value) {
        return new UserId(value);
    }

    /**
     * 生成新的UserId
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * 从UUID创建UserId
     */
    public static UserId fromUUID(UUID uuid) {
        return new UserId(uuid.toString().replace("-", ""));
    }

    /**
     * 获取值
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
