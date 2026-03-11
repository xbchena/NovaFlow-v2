package com.novaflow.infrastructure.persistence.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户持久化对象
 * 对应数据库表 users
 */
@Data
public class UserPO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 微信 OpenID
     */
    private String openid;

    /**
     * 微信 UnionID
     */
    private String unionid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户偏好设置 (JSON)
     */
    private String preferences;

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
