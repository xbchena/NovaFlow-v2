package com.novaflow.domain.model.recommendation;

import com.novaflow.domain.model.shared.aggregate.AggregateRoot;
import com.novaflow.domain.model.shared.valueobject.RecommendationId;
import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.model.shared.valueobject.VideoId;
import com.novaflow.domain.model.recommendation.event.*;
import com.novaflow.domain.model.recommendation.valueobject.*;
import com.novaflow.domain.model.recommendation.service.RecommendationGenerator;
import com.novaflow.domain.model.recommendation.service.DeduplicationService;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推荐聚合根
 * 管理推荐生成、去重和展示
 */
@Getter
public class Recommendation extends AggregateRoot {

    private final RecommendationId recommendationId;
    private final UserId userId;
    private final VideoId videoId;
    private final SceneAnalysis sceneAnalysis;
    private final RecommendationContext context;
    private final List<RecommendationItem> items;
    private final LocalDateTime createdAt;
    private Boolean deleted;

    // 私有构造函数
    private Recommendation(RecommendationId recommendationId, UserId userId, VideoId videoId,
                          SceneAnalysis sceneAnalysis, RecommendationContext context,
                          List<RecommendationItem> items) {
        this.recommendationId = recommendationId;
        this.userId = userId;
        this.videoId = videoId;
        this.sceneAnalysis = sceneAnalysis;
        this.context = context;
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }

    /**
     * 创建推荐（工厂方法）
     * 使用AI生成和去重逻辑
     */
    public static Recommendation create(UserId userId, VideoId videoId,
                                       SceneAnalysis sceneAnalysis,
                                       RecommendationContext context,
                                       RecommendationHistory history,
                                       RecommendationGenerator generator,
                                       DeduplicationService deduplicationService) {

        if (userId == null || videoId == null) {
            throw new IllegalArgumentException("用户ID和视频ID不能为空");
        }

        if (sceneAnalysis == null || !sceneAnalysis.isValid()) {
            throw new IllegalArgumentException("场景分析结果无效");
        }

        RecommendationId recommendationId = RecommendationId.generate();

        // 1. 使用AI生成初始推荐
        List<RecommendationItem> initialItems = generator.generate(sceneAnalysis, context);

        // 2. 使用去重服务过滤推荐
        List<RecommendationItem> finalItems = deduplicationService.deduplicate(
                initialItems,
                history,
                context,
                10 // 最多返回10个推荐
        );

        // 3. 创建推荐聚合
        Recommendation recommendation = new Recommendation(
                recommendationId,
                userId,
                videoId,
                sceneAnalysis,
                context,
                finalItems
        );

        // 4. 发布领域事件
        recommendation.addDomainEvent(new RecommendationGeneratedEvent(
                recommendationId.getValue(),
                userId.getValue(),
                videoId.getValue(),
                finalItems.size()
        ));

        return recommendation;
    }

    /**
     * 记录用户点击推荐项
     */
    public RecommendationItemClick recordItemClick(String foodName, String placeId) {
        // 验证推荐项是否存在
        boolean itemExists = items.stream()
                .anyMatch(item -> item.getFoodName().equals(foodName));

        if (!itemExists) {
            throw new IllegalArgumentException("推荐项不存在: " + foodName);
        }

        RecommendationItemClick click = RecommendationItemClick.create(
                this.recommendationId,
                this.userId,
                foodName,
                placeId
        );

        // 发布事件
        addDomainEvent(new RecommendationItemClickedEvent(
                this.recommendationId.getValue(),
                this.userId.getValue(),
                foodName,
                placeId
        ));

        return click;
    }

    /**
     * 获取高分推荐项（>=70分）
     */
    public List<RecommendationItem> getHighMatchItems() {
        return items.stream()
                .filter(RecommendationItem::isHighMatch)
                .collect(Collectors.toList());
    }

    /**
     * 获取中等匹配推荐项（40-69分）
     */
    public List<RecommendationItem> getMediumMatchItems() {
        return items.stream()
                .filter(RecommendationItem::isMediumMatch)
                .collect(Collectors.toList());
    }

    /**
     * 按匹配分数排序的推荐项
     */
    public List<RecommendationItem> getItemsSortedByScore() {
        return items.stream()
                .sorted(Comparator.comparing(RecommendationItem::getMatchScore).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 检查是否有推荐项
     */
    public boolean hasItems() {
        return !items.isEmpty();
    }

    /**
     * 获取推荐项数量
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * 检查是否属于指定用户
     */
    public boolean belongsToUser(UserId userId) {
        return this.userId.equals(userId);
    }

    /**
     * 检查是否属于指定视频
     */
    public boolean belongsToVideo(VideoId videoId) {
        return this.videoId.equals(videoId);
    }

    /**
     * 检查是否已删除
     */
    public boolean isDeleted() {
        return deleted != null && deleted;
    }

    /**
     * 软删除推荐
     */
    public void delete() {
        if (this.deleted) {
            throw new UnsupportedOperationException("推荐已被删除");
        }

        this.deleted = true;
        addDomainEvent(new RecommendationDeletedEvent(
                this.recommendationId.getValue(),
                this.userId.getValue()
        ));
    }

    @Override
    public String getId() {
        return recommendationId.getValue();
    }

    /**
     * 从数据库重建Recommendation聚合根
     */
    public static Recommendation reconstruct(
            RecommendationId recommendationId,
            UserId userId,
            VideoId videoId,
            SceneAnalysis sceneAnalysis,
            RecommendationContext context,
            List<RecommendationItem> items,
            LocalDateTime createdAt,
            Boolean deleted
    ) {
        Recommendation recommendation = new Recommendation(
                recommendationId,
                userId,
                videoId,
                sceneAnalysis,
                context,
                items
        );
        recommendation.deleted = deleted;
        return recommendation;
    }

    /**
     * 推荐项点击值对象
     */
    @Getter
    public static class RecommendationItemClick {
        private final String foodName;
        private final String placeId;
        private final LocalDateTime clickedAt;

        private RecommendationItemClick(String foodName, String placeId, LocalDateTime clickedAt) {
            this.foodName = foodName;
            this.placeId = placeId;
            this.clickedAt = clickedAt;
        }

        public static RecommendationItemClick create(
                RecommendationId recommendationId,
                UserId userId,
                String foodName,
                String placeId) {
            return new RecommendationItemClick(foodName, placeId, LocalDateTime.now());
        }
    }
}
