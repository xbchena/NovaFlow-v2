CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    diet_preferences JSON COMMENT '饮食偏好，如 ["辣味","火锅"]',
    allergies JSON COMMENT '过敏信息，如 ["花生","海鲜"]',
    preferred_cuisines JSON COMMENT '喜好菜系，如 ["川菜","湘菜"]',
    preferred_price_range VARCHAR(20) COMMENT '价格偏好：低/中/高',
    behavior_summary JSON COMMENT '行为摘要',
    total_recommendations INT DEFAULT 0 COMMENT '累计推荐次数',
    total_clicks INT DEFAULT 0 COMMENT '累计点击次数',
    updated_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表 - L3长期记忆';
