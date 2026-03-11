package com.novaflow.domain.model.video.valueobject;

import com.novaflow.domain.model.shared.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 视频元数据值对象
 * 包含视频的基本信息
 */
@Getter
@EqualsAndHashCode
public class VideoMetadata implements ValueObject {

    private final Integer duration; // 秒
    private final Long fileSize;    // 字节
    private final String format;
    private final Integer width;
    private final Integer height;
    private final String thumbnailUrl;

    private VideoMetadata(Integer duration, Long fileSize, String format,
                         Integer width, Integer height, String thumbnailUrl) {
        if (duration != null && duration <= 0) {
            throw new IllegalArgumentException("视频时长必须大于0");
        }
        if (fileSize != null && fileSize <= 0) {
            throw new IllegalArgumentException("文件大小必须大于0");
        }
        this.duration = duration;
        this.fileSize = fileSize;
        this.format = format;
        this.width = width;
        this.height = height;
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * 创建基本的视频元数据
     */
    public static VideoMetadata of(Integer duration, Long fileSize) {
        return new VideoMetadata(duration, fileSize, null, null, null, null);
    }

    /**
     * 创建完整的视频元数据
     */
    public static VideoMetadata of(Integer duration, Long fileSize, String format,
                                   Integer width, Integer height, String thumbnailUrl) {
        return new VideoMetadata(duration, fileSize, format, width, height, thumbnailUrl);
    }

    /**
     * 检查是否有缩略图
     */
    public boolean hasThumbnail() {
        return thumbnailUrl != null && !thumbnailUrl.trim().isEmpty();
    }

    /**
     * 检查是否有分辨率信息
     */
    public boolean hasResolution() {
        return width != null && height != null && width > 0 && height > 0;
    }

    /**
     * 获取分辨率描述
     */
    public String getResolutionDescription() {
        if (!hasResolution()) {
            return "未知";
        }
        return width + "x" + height;
    }

    /**
     * 获取文件大小描述（人类可读）
     */
    public String getFileSizeDescription() {
        if (fileSize == null) {
            return "未知";
        }

        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024 * 1024));
        }
    }
}
