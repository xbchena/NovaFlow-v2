package com.novaflow.infrastructure.persistence.impl;

import com.novaflow.domain.model.recommendation.Recommendation;
import com.novaflow.domain.model.recommendation.valueobject.RecommendationContext;
import com.novaflow.domain.model.recommendation.valueobject.RecommendationItem;
import com.novaflow.domain.model.recommendation.valueobject.SceneAnalysis;
import com.novaflow.domain.model.shared.valueobject.RecommendationId;
import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.model.shared.valueobject.VideoId;
import com.novaflow.domain.repository.RecommendationRepository;
import com.novaflow.infrastructure.persistence.mapper.RecommendationMapper;
import com.novaflow.infrastructure.persistence.po.RecommendationPO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 推荐仓储实现
 * 使用 MyBatis XML 方式操作数据库
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RecommendationRepositoryImpl implements RecommendationRepository {

    private static final Logger log = LoggerFactory.getLogger(RecommendationRepositoryImpl.class);

    private final RecommendationMapper recommendationMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Recommendation> findById(RecommendationId recommendationId) {
        RecommendationPO recommendationPO = recommendationMapper.selectById(
                Long.parseLong(recommendationId.getValue())
        );
        return Optional.ofNullable(toDomain(recommendationPO));
    }

    @Override
    public List<Recommendation> findByUserId(UserId userId) {
        List<RecommendationPO> recommendationPOList = recommendationMapper.selectByUserId(
                Long.parseLong(userId.getValue())
        );
        return recommendationPOList.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Recommendation> findByUserId(UserId userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<RecommendationPO> recommendationPOList = recommendationMapper.selectByUserIdAndPage(
                Long.parseLong(userId.getValue()),
                offset,
                pageSize
        );
        return recommendationPOList.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Recommendation> findByVideoId(VideoId videoId) {
        RecommendationPO recommendationPO = recommendationMapper.selectByVideoId(
                Long.parseLong(videoId.getValue())
        );
        return Optional.ofNullable(toDomain(recommendationPO));
    }

    @Override
    public Optional<Recommendation> findLatestByUserId(UserId userId) {
        RecommendationPO recommendationPO = recommendationMapper.selectLatestByUserId(
                Long.parseLong(userId.getValue())
        );
        return Optional.ofNullable(toDomain(recommendationPO));
    }

    @Override
    public long countByUserId(UserId userId) {
        return recommendationMapper.countByUserId(Long.parseLong(userId.getValue()));
    }

    @Override
    public Recommendation save(Recommendation recommendation) {
        RecommendationPO recommendationPO = toPO(recommendation);

        if (recommendationPO.getId() == null) {
            // 新增
            recommendationPO.setCreatedAt(LocalDateTime.now());
            recommendationPO.setUpdatedAt(LocalDateTime.now());
            recommendationPO.setDeleted(false);
            recommendationMapper.insert(recommendationPO);
        } else {
            // 更新
            recommendationPO.setUpdatedAt(LocalDateTime.now());
            recommendationMapper.update(recommendationPO);
        }

        return toDomain(recommendationPO);
    }

    @Override
    public void deleteById(RecommendationId recommendationId) {
        recommendationMapper.deleteById(Long.parseLong(recommendationId.getValue()));
    }

    @Override
    public List<Recommendation> findAll() {
        // 默认返回前100条，使用第一个用户的ID作为示例
        // 在实际应用中，应该有一个专门的不带用户ID的查询方法
        // 这里暂时返回空列表
        return List.of();
    }

    /**
     * PO 转领域对象
     */
    private Recommendation toDomain(RecommendationPO recommendationPO) {
        if (recommendationPO == null) {
            return null;
        }

        try {
            // 创建 SceneAnalysis
            SceneAnalysis sceneAnalysis = SceneAnalysis.of(
                    recommendationPO.getSceneType(),
                    recommendationPO.getSceneDescription(),
                    List.of(),
                    List.of()
            );

            // 创建 RecommendationContext
            RecommendationContext context = RecommendationContext.empty();

            // 解析推荐项列表
            List<RecommendationItem> items = List.of();

            return Recommendation.reconstruct(
                    RecommendationId.of(recommendationPO.getId().toString()),
                    UserId.of(recommendationPO.getUserId().toString()),
                    recommendationPO.getVideoId() != null ?
                            VideoId.of(recommendationPO.getVideoId().toString()) : null,
                    sceneAnalysis,
                    context,
                    items,
                    recommendationPO.getCreatedAt(),
                    recommendationPO.getDeleted()
            );
        } catch (Exception e) {
            log.error("Failed to convert RecommendationPO to Recommendation domain object", e);
            throw new RuntimeException("数据转换失败", e);
        }
    }

    /**
     * 领域对象转 PO
     */
    private RecommendationPO toPO(Recommendation recommendation) {
        if (recommendation == null) {
            return null;
        }

        RecommendationPO recommendationPO = new RecommendationPO();

        if (recommendation.getId() != null) {
            recommendationPO.setId(Long.parseLong(recommendation.getId()));
        }

        if (recommendation.getUserId() != null) {
            recommendationPO.setUserId(Long.parseLong(recommendation.getUserId().getValue()));
        }

        if (recommendation.getVideoId() != null) {
            recommendationPO.setVideoId(Long.parseLong(recommendation.getVideoId().getValue()));
        }

        // 从 SceneAnalysis 获取场景信息
        if (recommendation.getSceneAnalysis() != null) {
            recommendationPO.setSceneType(recommendation.getSceneAnalysis().sceneType());
            recommendationPO.setSceneDescription(recommendation.getSceneAnalysis().sceneDescription());
        }

        try {
            recommendationPO.setContent(objectMapper.writeValueAsString(recommendation.getItems()));
        } catch (Exception e) {
            log.error("Failed to serialize recommendation content", e);
        }

        recommendationPO.setCreatedAt(recommendation.getCreatedAt());
        recommendationPO.setUpdatedAt(LocalDateTime.now());
        recommendationPO.setDeleted(recommendation.isDeleted());

        return recommendationPO;
    }
}
