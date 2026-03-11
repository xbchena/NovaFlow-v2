package com.novaflow.infrastructure.persistence.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐持久化对象
 * 对应数据库表 recommendations
 */
@Data
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
}
