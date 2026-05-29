package com.novaflow.recommendation.domain.repository;

import com.novaflow.common.domain.valueobject.UserId;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.RecommendationHistory;

import java.util.Optional;

/**
 * 推荐历史仓储接口
 * 使用Redis存储用户的推荐历史，用于去重
 */
public interface RecommendationHistoryRepository {

    /**
     * 获取用户的推荐历史
     */
    Optional<RecommendationHistory> findByUserId(UserId userId);

    /**
     * 保存或更新用户的推荐历史
     */
    void save(UserId userId, RecommendationHistory history);

    /**
     * 删除用户的推荐历史
     */
    void deleteByUserId(UserId userId);

    /**
     * 清理过期的推荐历史
     */
    void cleanExpiredHistory(UserId userId, long expireDays);
}
