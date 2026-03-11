package com.novaflow.domain.model.video;

import com.novaflow.domain.model.shared.aggregate.AggregateRoot;
import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.model.shared.valueobject.VideoId;
import com.novaflow.domain.model.video.event.*;
import com.novaflow.domain.model.video.exception.*;
import com.novaflow.domain.model.video.valueobject.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 视频聚合根
 * 管理视频的上传、处理和状态转换
 */
@Getter
public class Video extends AggregateRoot {

    private final VideoId videoId;
    private final UserId userId;
    private VideoStatus status;
    private OSSStorageInfo storageInfo;
    private VideoMetadata metadata;
    private Location location;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

    // 私有构造函数
    private Video(VideoId videoId, UserId userId, VideoStatus status, VideoMetadata metadata, Location location) {
        this.videoId = videoId;
        this.userId = userId;
        this.status = status;
        this.metadata = metadata;
        this.location = location;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleted = false;
    }

    /**
     * 创建新视频（工厂方法）
     */
    public static Video create(UserId userId, VideoMetadata metadata, Location location) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        VideoId videoId = VideoId.generate();
        Video video = new Video(videoId, userId, VideoStatus.initialized(), metadata, location);

        video.addDomainEvent(new VideoCreatedEvent(videoId.getValue(), userId.getValue()));

        return video;
    }

    /**
     * 开始上传视频到OSS
     */
    public void startUploading(OSSStorageInfo storageInfo) {
        if (!status.canTransitionTo(VideoStatus.uploading())) {
            throw new InvalidVideoStatusException(status.getValue(), "UPLOADING");
        }

        this.status = VideoStatus.uploading();
        this.storageInfo = storageInfo;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new VideoUploadStartedEvent(videoId.getValue(), userId.getValue()));
    }

    /**
     * 标记上传完成，开始处理
     */
    public void markAsUploaded() {
        if (!status.isUploading()) {
            throw new InvalidVideoStatusException("当前状态不是上传中");
        }

        this.status = VideoStatus.processing();
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new VideoUploadCompletedEvent(videoId.getValue(), userId.getValue()));
    }

    /**
     * 标记处理完成
     */
    public void markAsCompleted() {
        if (!status.isProcessing()) {
            throw new InvalidVideoStatusException("当前状态不是处理中");
        }

        this.status = VideoStatus.completed();
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new VideoProcessingCompletedEvent(videoId.getValue(), userId.getValue()));
    }

    /**
     * 标记处理失败
     */
    public void markAsFailed(String errorMessage) {
        if (status.isTerminal()) {
            throw new InvalidVideoStatusException("视频已处于终态");
        }

        this.status = VideoStatus.failed();
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new VideoProcessingFailedEvent(videoId.getValue(), userId.getValue(), errorMessage));
    }

    /**
     * 更新缩略图URL
     */
    public void updateThumbnail(String thumbnailUrl) {
        if (thumbnailUrl == null || thumbnailUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("缩略图URL不能为空");
        }

        if (this.metadata == null) {
            this.metadata = VideoMetadata.of(null, null);
        }

        // 重新创建元数据以更新缩略图
        this.metadata = VideoMetadata.of(
                this.metadata.getDuration(),
                this.metadata.getFileSize(),
                this.metadata.getFormat(),
                this.metadata.getWidth(),
                this.metadata.getHeight(),
                thumbnailUrl
        );

        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新位置信息
     */
    public void updateLocation(Location newLocation) {
        if (newLocation == null) {
            throw new IllegalArgumentException("位置信息不能为空");
        }

        this.location = newLocation;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新视频元数据
     */
    public void updateMetadata(VideoMetadata newMetadata) {
        if (newMetadata == null) {
            throw new IllegalArgumentException("视频元数据不能为空");
        }

        this.metadata = newMetadata;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 检查是否属于指定用户
     */
    public boolean belongsToUser(UserId userId) {
        return this.userId.equals(userId);
    }

    /**
     * 检查是否可以被删除
     */
    public boolean canBeDeleted() {
        return status.isTerminal() || status.isUploading();
    }

    /**
     * 软删除视频
     */
    public void delete() {
        if (!canBeDeleted()) {
            throw new InvalidVideoStatusException("视频正在处理中，无法删除");
        }

        if (this.deleted) {
            throw new UnsupportedOperationException("视频已被删除");
        }

        this.deleted = true;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new VideoDeletedEvent(videoId.getValue(), userId.getValue()));
    }

    /**
     * 检查是否已删除
     */
    public boolean isDeleted() {
        return deleted != null && deleted;
    }

    /**
     * 获取错误信息
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 检查是否有错误
     */
    public boolean hasError() {
        return errorMessage != null && !errorMessage.trim().isEmpty();
    }

    @Override
    public String getId() {
        return videoId.getValue();
    }

    /**
     * 获取视频ID (Lombok 风格)
     */
    public VideoId getVideoId() {
        return videoId;
    }

    /**
     * 获取用户ID (Lombok 风格)
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * 获取状态 (Lombok 风格)
     */
    public VideoStatus getStatus() {
        return status;
    }

    /**
     * 获取元数据 (Lombok 风格)
     */
    public VideoMetadata getMetadata() {
        return metadata;
    }

    /**
     * 获取位置信息 (Lombok 风格)
     */
    public Location getLocation() {
        return location;
    }

    /**
     * 获取存储信息 (Lombok 风格)
     */
    public OSSStorageInfo getStorageInfo() {
        return storageInfo;
    }

    /**
     * 获取创建时间 (Lombok 风格)
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 获取更新时间 (Lombok 风格)
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 从数据库重建Video聚合根
     */
    public static Video reconstruct(
            VideoId videoId,
            UserId userId,
            VideoStatus status,
            OSSStorageInfo storageInfo,
            VideoMetadata metadata,
            Location location,
            String errorMessage,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Boolean deleted
    ) {
        Video video = new Video(videoId, userId, status, metadata, location);
        video.storageInfo = storageInfo;
        video.errorMessage = errorMessage;
        video.createdAt = createdAt;
        video.updatedAt = updatedAt;
        video.deleted = deleted;
        return video;
    }
}
