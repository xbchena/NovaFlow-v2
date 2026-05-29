package com.novaflow.recommendation.domain.model.recommendation.valueobject;

import com.novaflow.common.domain.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 场景分析结果值对象
 * 包含AI对视频场景的分析结果
 */
@Getter
@EqualsAndHashCode
public class SceneAnalysis implements ValueObject {

    private final String sceneType;       // 场景类型：餐厅、街边、家庭等
    private final String sceneDescription;// 场景描述
    private final List<String> detectedObjects; // 检测到的物体
    private final List<String> foodItems; // 识别的食物
    private final LocalDateTime analyzedAt;
    private final Map<String, Object> metadata; // 额外元数据

    private SceneAnalysis(String sceneType, String sceneDescription, List<String> detectedObjects,
                         List<String> foodItems, LocalDateTime analyzedAt, Map<String, Object> metadata) {
        if (sceneType == null || sceneType.trim().isEmpty()) {
            throw new IllegalArgumentException("场景类型不能为空");
        }
        this.sceneType = sceneType;
        this.sceneDescription = sceneDescription;
        this.detectedObjects = detectedObjects == null ? List.of() : List.copyOf(detectedObjects);
        this.foodItems = foodItems == null ? List.of() : List.copyOf(foodItems);
        this.analyzedAt = analyzedAt != null ? analyzedAt : LocalDateTime.now();
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    /**
     * 创建场景分析结果
     */
    public static SceneAnalysis of(String sceneType, String sceneDescription,
                                   List<String> detectedObjects, List<String> foodItems) {
        return new SceneAnalysis(sceneType, sceneDescription, detectedObjects,
                foodItems, LocalDateTime.now(), Map.of());
    }

    /**
     * 创建完整的场景分析结果
     */
    public static SceneAnalysis of(String sceneType, String sceneDescription,
                                   List<String> detectedObjects, List<String> foodItems,
                                   Map<String, Object> metadata) {
        return new SceneAnalysis(sceneType, sceneDescription, detectedObjects,
                foodItems, LocalDateTime.now(), metadata);
    }

    /**
     * 从数据库JSON重建
     */
    public static SceneAnalysis reconstruct(String sceneType, String sceneDescription,
                                           List<String> detectedObjects, List<String> foodItems,
                                           LocalDateTime analyzedAt, Map<String, Object> metadata) {
        return new SceneAnalysis(sceneType, sceneDescription, detectedObjects,
                foodItems, analyzedAt, metadata);
    }

    /**
     * 检查是否为餐厅场景
     */
    public boolean isRestaurantScene() {
        return "餐厅".equals(sceneType) || "restaurant".equalsIgnoreCase(sceneType);
    }

    /**
     * 检查是否为街边场景
     */
    public boolean isStreetScene() {
        return "街边".equals(sceneType) || "street".equalsIgnoreCase(sceneType);
    }

    /**
     * 检查是否为家庭场景
     */
    public boolean isHomeScene() {
        return "家庭".equals(sceneType) || "home".equalsIgnoreCase(sceneType);
    }

    /**
     * 检查是否检测到特定物体
     */
    public boolean hasDetectedObject(String object) {
        return detectedObjects.contains(object);
    }

    /**
     * 检查是否识别到特定食物
     */
    public boolean hasFoodItem(String food) {
        return foodItems.contains(food);
    }

    /**
     * 检查分析是否有效（有足够的信息）
     */
    public boolean isValid() {
        return sceneType != null && !sceneType.trim().isEmpty() &&
               (!detectedObjects.isEmpty() || !foodItems.isEmpty());
    }

    /**
     * 获取元数据值
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, Class<T> type) {
        return (T) metadata.get(key);
    }

    /**
     * 获取场景类型
     */
    public String sceneType() {
        return sceneType;
    }

    /**
     * 获取场景描述
     */
    public String sceneDescription() {
        return sceneDescription;
    }

    /**
     * 获取检测到的物体
     */
    public List<String> detectedObjects() {
        return detectedObjects;
    }

    /**
     * 获取识别的食物
     */
    public List<String> foodItems() {
        return foodItems;
    }

    // Explicit getters for Lombok compatibility (JavaBean convention)
    public String getSceneType() {
        return sceneType;
    }

    public String getSceneDescription() {
        return sceneDescription;
    }

    public List<String> getDetectedObjects() {
        return detectedObjects;
    }

    public List<String> getFoodItems() {
        return foodItems;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
