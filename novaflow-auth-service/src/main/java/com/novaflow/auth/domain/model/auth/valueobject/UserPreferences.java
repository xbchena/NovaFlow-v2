package com.novaflow.auth.domain.model.auth.valueobject;

import com.novaflow.common.domain.valueobject.ValueObject;
import com.alibaba.fastjson2.JSON;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

/**
 * 用户偏好设置值对象
 * 存储用户的个人偏好配置
 */
@Getter
@EqualsAndHashCode
public class UserPreferences implements ValueObject {

    private final Map<String, Object> preferences;

    private UserPreferences(Map<String, Object> preferences) {
        this.preferences = preferences == null ? Map.of() : Map.copyOf(preferences);
    }

    /**
     * 从Map创建用户偏好
     */
    public static UserPreferences of(Map<String, Object> preferences) {
        return new UserPreferences(preferences);
    }

    /**
     * 创建空的偏好设置
     */
    public static UserPreferences empty() {
        return new UserPreferences(Map.of());
    }

    /**
     * 从JSON字符串创建
     */
    public static UserPreferences fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return empty();
        }
        try {
            Map<String, Object> map = JSON.parseObject(json, Map.class);
            return new UserPreferences(map);
        } catch (Exception e) {
            return empty();
        }
    }

    /**
     * 获取偏好值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = preferences.get(key);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    /**
     * 获取偏好值，带默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type, T defaultValue) {
        Object value = preferences.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    /**
     * 获取偏好值（字符串）
     */
    public String getString(String key) {
        return get(key, String.class);
    }

    /**
     * 获取偏好值（整数）
     */
    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    /**
     * 获取偏好值（布尔）
     */
    public Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    /**
     * 检查是否包含某个偏好
     */
    public boolean contains(String key) {
        return preferences.containsKey(key);
    }

    /**
     * 转换为JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(preferences);
    }

    /**
     * 创建新的偏好设置（添加或更新）
     */
    public UserPreferences with(String key, Object value) {
        Map<String, Object> newPrefs = Map.copyOf(preferences);
        return new UserPreferences(newPrefs);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
