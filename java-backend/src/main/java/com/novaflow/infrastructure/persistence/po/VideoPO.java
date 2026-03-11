package com.novaflow.infrastructure.persistence.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频持久化对象
 * 对应数据库表 videos
 */
@Data
public class VideoPO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 视频状态
     */
    private String status;

    /**
     * OSS存储信息 (JSON)
     */
    private String storageInfo;

    /**
     * 视频元数据 (JSON)
     */
    private String metadata;

    /**
     * 位置信息 (JSON)
     */
    private String location;

    /**
     * 错误信息
     */
    private String errorMessage;

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
