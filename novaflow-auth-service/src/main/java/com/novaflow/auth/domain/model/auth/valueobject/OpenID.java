package com.novaflow.auth.domain.model.auth.valueobject;

import com.novaflow.common.domain.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * 微信OpenID值对象
 * 表示微信应用内的唯一用户标识
 */
@Getter
@EqualsAndHashCode
public class OpenID implements ValueObject {

    private final String value;

    private OpenID(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("OpenID不能为空");
        }
        if (value.length() > 128) {
            throw new IllegalArgumentException("OpenID长度不能超过128个字符");
        }
        this.value = value;
    }

    /**
     * 从字符串创建OpenID
     */
    public static OpenID of(String value) {
        return new OpenID(value);
    }

    /**
     * 验证OpenID格式是否有效
     */
    public boolean isValid() {
        return value != null && !value.trim().isEmpty();
    }

    @Override
    public String toString() {
        // 脱敏处理，用于日志输出
        if (value.length() > 8) {
            return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
        }
        return "****";
    }

    /**
     * 获取值
     */
    public String getValue() {
        return value;
    }
}
