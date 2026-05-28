package com.novaflow.auth.domain.model.auth.valueobject;

import com.novaflow.common.domain.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * 微信UnionID值对象
 * 表示同一开放平台下应用的唯一用户标识
 */
@Getter
@EqualsAndHashCode
public class UnionID implements ValueObject {

    private final String value;

    private UnionID(String value) {
        // UnionID是可选的，可以为null
        if (value != null && value.length() > 128) {
            throw new IllegalArgumentException("UnionID长度不能超过128个字符");
        }
        this.value = value;
    }

    // Explicit getter for Lombok compatibility
    public String getValue() {
        return value;
    }

    /**
     * 从字符串创建UnionID
     * 允许null值
     */
    public static UnionID of(String value) {
        return new UnionID(value);
    }

    /**
     * 验证UnionID是否存在
     */
    public boolean isPresent() {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * 验证UnionID格式是否有效
     */
    public boolean isValid() {
        return isPresent();
    }

    @Override
    public String toString() {
        if (!isPresent()) {
            return "未绑定";
        }
        // 脱敏处理
        if (value.length() > 8) {
            return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
        }
        return "****";
    }
}
