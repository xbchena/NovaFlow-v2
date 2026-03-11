package com.novaflow.domain.model.recommendation.valueobject;

import com.novaflow.domain.model.shared.valueobject.ValueObject;
import com.novaflow.domain.model.video.valueobject.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 推荐上下文值对象
 * 包含生成推荐时的上下文信息
 */
@Getter
@EqualsAndHashCode
public class RecommendationContext implements ValueObject {

    private final Location location;
    private final LocalDateTime requestTime;
    private final String timeOfDay;      // 早餐、午餐、晚餐、夜宵
    private final String weather;        // 天气情况
    private final Map<String, Object> userContext; // 用户上下文（偏好、历史等）

    private RecommendationContext(Location location, LocalDateTime requestTime,
                                  String timeOfDay, String weather, Map<String, Object> userContext) {
        this.location = location;
        this.requestTime = requestTime != null ? requestTime : LocalDateTime.now();
        this.timeOfDay = timeOfDay;
        this.weather = weather;
        this.userContext = userContext == null ? Map.of() : Map.copyOf(userContext);
    }

    /**
     * 创建推荐上下文
     */
    public static RecommendationContext of(Location location) {
        return new RecommendationContext(location, LocalDateTime.now(), null, null, Map.of());
    }

    /**
     * 创建完整的推荐上下文
     */
    public static RecommendationContext of(Location location, String timeOfDay,
                                          String weather, Map<String, Object> userContext) {
        return new RecommendationContext(location, LocalDateTime.now(), timeOfDay, weather, userContext);
    }

    /**
     * 创建空上下文
     */
    public static RecommendationContext empty() {
        return new RecommendationContext(Location.empty(), LocalDateTime.now(), null, null, Map.of());
    }

    /**
     * 检查是否有位置信息
     */
    public boolean hasLocation() {
        return location != null && location.hasLocation();
    }

    /**
     * 检查是否为早餐时间
     */
    public boolean isBreakfastTime() {
        return "早餐".equals(timeOfDay) || "breakfast".equalsIgnoreCase(timeOfDay);
    }

    /**
     * 检查是否为午餐时间
     */
    public boolean isLunchTime() {
        return "午餐".equals(timeOfDay) || "lunch".equalsIgnoreCase(timeOfDay);
    }

    /**
     * 检查是否为晚餐时间
     */
    public boolean isDinnerTime() {
        return "晚餐".equals(timeOfDay) || "dinner".equalsIgnoreCase(timeOfDay);
    }

    /**
     * 检查是否为夜宵时间
     */
    public boolean isLateNightSnackTime() {
        return "夜宵".equals(timeOfDay) || "late_night_snack".equalsIgnoreCase(timeOfDay);
    }

    /**
     * 获取用户上下文值
     */
    @SuppressWarnings("unchecked")
    public <T> T getUserContext(String key, Class<T> type) {
        return (T) userContext.get(key);
    }
}
