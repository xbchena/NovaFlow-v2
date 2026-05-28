package com.novaflow.common.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

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

    public static VideoId of(String value) {
        return new VideoId(value);
    }

    public static VideoId generate() {
        return new VideoId(UUID.randomUUID().toString().replace("-", ""));
    }

    public static VideoId fromUUID(UUID uuid) {
        return new VideoId(uuid.toString().replace("-", ""));
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
