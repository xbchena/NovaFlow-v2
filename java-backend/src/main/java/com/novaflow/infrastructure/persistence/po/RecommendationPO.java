package com.novaflow.infrastructure.persistence.po;

import java.time.LocalDateTime;

/**
 * 推荐持久化对象
 * 对应数据库表 recommendations
 */
public class RecommendationPO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 推荐内容 (JSON)
     */
    private String content;

    /**
     * 场景类型
     */
    private String sceneType;

    /**
     * 场景描述
     */
    private String sceneDescription;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否删除
     */
    private Boolean deleted;

    // Getters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public String getContent() {
        return content;
    }

    public String getSceneType() {
        return sceneType;
    }

    public String getSceneDescription() {
        return sceneDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public void setSceneDescription(String sceneDescription) {
        this.sceneDescription = sceneDescription;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
