package com.novaflow.domain.model.feedback.valueobject;

import com.novaflow.domain.model.shared.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 选中项值对象
 * 表示用户选择的推荐项
 */
@Getter
@EqualsAndHashCode
public class SelectedItem implements ValueObject {

    private final String foodName;
    private final String placeId;
    private final String placeName;
    private final LocalDateTime selectedAt;

    private SelectedItem(String foodName, String placeId, String placeName, LocalDateTime selectedAt) {
        if (foodName == null || foodName.trim().isEmpty()) {
            throw new IllegalArgumentException("食物名称不能为空");
        }
        this.foodName = foodName;
        this.placeId = placeId;
        this.placeName = placeName;
        this.selectedAt = selectedAt != null ? selectedAt : LocalDateTime.now();
    }

    /**
     * 创建选中项
     */
    public static SelectedItem of(String foodName, String placeId, String placeName) {
        return new SelectedItem(foodName, placeId, placeName, LocalDateTime.now());
    }

    /**
     * 创建不带地点的选中项
     */
    public static SelectedItem of(String foodName) {
        return new SelectedItem(foodName, null, null, LocalDateTime.now());
    }

    /**
     * 检查是否选择了地点
     */
    public boolean hasPlace() {
        return placeId != null && !placeId.trim().isEmpty();
    }

    /**
     * 获取地点描述
     */
    public String getPlaceDescription() {
        if (!hasPlace()) {
            return "未选择地点";
        }
        return placeName != null ? placeName : placeId;
    }

    /**
     * 获取食物名称
     */
    public String foodName() {
        return foodName;
    }

    /**
     * 获取地点ID
     */
    public String placeId() {
        return placeId;
    }

    /**
     * 获取地点名称
     */
    public String placeName() {
        return placeName;
    }

    /**
     * 获取选择时间
     */
    public LocalDateTime selectedAt() {
        return selectedAt;
    }
}
