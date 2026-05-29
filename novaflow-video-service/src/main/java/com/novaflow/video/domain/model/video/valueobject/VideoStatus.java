package com.novaflow.video.domain.model.video.valueobject;

import com.novaflow.common.domain.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 视频状态值对象
 * 表示视频在处理流程中的当前状态
 */
@Getter
@EqualsAndHashCode
public class VideoStatus implements ValueObject {

    private final String value;

    private VideoStatus(String value) {
        this.value = value;
    }

    /**
     * 已初始化状态
     */
    public static VideoStatus initialized() {
        return new VideoStatus("INITIALIZED");
    }

    /**
     * 上传中状态
     */
    public static VideoStatus uploading() {
        return new VideoStatus("UPLOADING");
    }

    /**
     * 处理中状态
     */
    public static VideoStatus processing() {
        return new VideoStatus("PROCESSING");
    }

    /**
     * 已完成状态
     */
    public static VideoStatus completed() {
        return new VideoStatus("COMPLETED");
    }

    /**
     * 失败状态
     */
    public static VideoStatus failed() {
        return new VideoStatus("FAILED");
    }

    /**
     * 从字符串创建状态
     */
    public static VideoStatus of(String value) {
        return new VideoStatus(value);
    }

    /**
     * 检查是否为初始状态
     */
    public boolean isInitialized() {
        return "INITIALIZED".equals(value);
    }

    /**
     * 检查是否正在上传
     */
    public boolean isUploading() {
        return "UPLOADING".equals(value);
    }

    /**
     * 检查是否正在处理
     */
    public boolean isProcessing() {
        return "PROCESSING".equals(value);
    }

    /**
     * 检查是否已完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(value);
    }

    /**
     * 检查是否失败
     */
    public boolean isFailed() {
        return "FAILED".equals(value);
    }

    /**
     * 检查是否为终态（已完成或失败）
     */
    public boolean isTerminal() {
        return isCompleted() || isFailed();
    }

    /**
     * 检查是否可以进行状态转换
     */
    public boolean canTransitionTo(VideoStatus newStatus) {
        return switch (this.value) {
            case "INITIALIZED" -> "UPLOADING".equals(newStatus.value);
            case "UPLOADING" -> "PROCESSING".equals(newStatus.value) || "FAILED".equals(newStatus.value);
            case "PROCESSING" -> "COMPLETED".equals(newStatus.value) || "FAILED".equals(newStatus.value);
            default -> false; // 终态不能转换
        };
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
