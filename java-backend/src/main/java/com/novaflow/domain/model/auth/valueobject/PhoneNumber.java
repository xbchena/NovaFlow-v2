package com.novaflow.domain.model.auth.valueobject;

import com.novaflow.domain.model.shared.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * 手机号值对象
 * 表示用户的手机号码
 */
@Getter
@EqualsAndHashCode
public class PhoneNumber implements ValueObject {

    private final String value;

    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";

    private PhoneNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (!value.matches(PHONE_PATTERN)) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
        this.value = value;
    }

    /**
     * 从字符串创建手机号
     */
    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    /**
     * 验证手机号格式是否有效
     */
    public static boolean isValid(String value) {
        return value != null && value.matches(PHONE_PATTERN);
    }

    /**
     * 获取脱敏的手机号（用于显示）
     */
    public String getMasked() {
        if (value.length() == 11) {
            return value.substring(0, 3) + "****" + value.substring(7);
        }
        return "****";
    }

    @Override
    public String toString() {
        return getMasked();
    }
}
