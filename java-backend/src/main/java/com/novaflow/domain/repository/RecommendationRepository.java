package com.novaflow.domain.repository;

import com.novaflow.domain.model.recommendation.Recommendation;
import com.novaflow.domain.model.shared.valueobject.RecommendationId;
import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.model.shared.valueobject.VideoId;

import java.util.List;
import java.util.Optional;

/**
 * 推荐仓储接口
 * 定义推荐聚合的持久化操作
 */
public interface RecommendationRepository extends Repository<Recommendation, RecommendationId> {

    /**
     * 根据用户ID查找推荐列表
     */
    List<Recommendation> findByUserId(UserId userId);

    /**
     * 根据用户ID分页查找推荐列表
     */
    List<Recommendation> findByUserId(UserId userId, int page, int pageSize);

    /**
     * 根据视频ID查找推荐
     */
    Optional<Recommendation> findByVideoId(VideoId videoId);

    /**
     * 查找用户的最新推荐
     */
    Optional<Recommendation> findLatestByUserId(UserId userId);

    /**
     * 统计用户的推荐数量
     */
    long countByUserId(UserId userId);
}
