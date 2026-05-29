package com.novaflow.feedback.infra.persistence.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户选择记录持久化对象
 * 对应数据库表 user_selections
 */
@Data
public class UserSelectionPO {

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
     * 推荐ID
     */
    private Long recommendationId;

    /**
     * 选择的食物名称
     */
    private String foodName;

    /**
     * 是否接受推荐
     */
    private Boolean accepted;

    /**
     * 反馈内容
     */
    private String feedback;

    /**
     * 选择时间
     */
    private LocalDateTime selectedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 是否删除
     */
    private Boolean deleted;

    // 显式 getter/setter 方法
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public Long getRecommendationId() {
        return recommendationId;
    }

    public String getFoodName() {
        return foodName;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public String getFeedback() {
        return feedback;
    }

    public LocalDateTime getSelectedAt() {
        return selectedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public void setRecommendationId(Long recommendationId) {
        this.recommendationId = recommendationId;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setSelectedAt(LocalDateTime selectedAt) {
        this.selectedAt = selectedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
