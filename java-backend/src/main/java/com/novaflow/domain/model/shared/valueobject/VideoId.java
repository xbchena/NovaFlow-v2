package com.novaflow.domain.model.shared.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 视频标识值对象
 * 表示系统中唯一的视频标识
 */
@Getter
@EqualsAndHashCode
public class VideoId {

    private final String value;

    private VideoId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("视频ID不能为空");
        }
        this.value = value;
    }

    /**
     * 从现有字符串创建VideoId
     */
    public static VideoId of(String value) {
        return new VideoId(value);
    }

    /**
     * 生成新的VideoId
     */
    public static VideoId generate() {
        return new VideoId(UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * 从UUID创建VideoId
     */
    public static VideoId fromUUID(UUID uuid) {
        return new VideoId(uuid.toString().replace("-", ""));
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
