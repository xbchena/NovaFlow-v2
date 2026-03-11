package com.novaflow.infrastructure.persistence.po;

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
}
