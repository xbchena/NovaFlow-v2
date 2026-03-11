package com.novaflow.domain.model.shared.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 用户选择标识值对象
 * 表示系统中唯一的用户选择标识
 */
@Getter
@EqualsAndHashCode
public class SelectionId {

    private final String value;

    private SelectionId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("选择ID不能为空");
        }
        this.value = value;
    }

    /**
     * 从现有字符串创建SelectionId
     */
    public static SelectionId of(String value) {
        return new SelectionId(value);
    }

    /**
     * 生成新的SelectionId
     */
    public static SelectionId generate() {
        return new SelectionId(UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * 从UUID创建SelectionId
     */
    public static SelectionId fromUUID(UUID uuid) {
        return new SelectionId(uuid.toString().replace("-", ""));
    }

    @Override
    public String toString() {
        return value;
    }
}
