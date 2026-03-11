package com.novaflow.domain.repository;

import com.novaflow.domain.model.feedback.UserSelection;
import com.novaflow.domain.model.shared.valueobject.RecommendationId;
import com.novaflow.domain.model.shared.valueobject.SelectionId;
import com.novaflow.domain.model.shared.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 用户选择仓储接口
 * 定义用户选择聚合的持久化操作
 */
public interface UserSelectionRepository extends Repository<UserSelection, SelectionId> {

    /**
     * 根据用户ID查找选择列表
     */
    List<UserSelection> findByUserId(UserId userId);

    /**
     * 根据推荐ID查找选择
     */
    Optional<UserSelection> findByRecommendationId(RecommendationId recommendationId);

    /**
     * 查找用户的最新选择
     */
    Optional<UserSelection> findLatestByUserId(UserId userId);

    /**
     * 统计用户的选择数量
     */
    long countByUserId(UserId userId);

    /**
     * 检查用户是否对推荐做出过选择
     */
    boolean existsByUserIdAndRecommendationId(UserId userId, RecommendationId recommendationId);
}
