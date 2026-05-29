package com.novaflow.recommendation.application.service;

import com.novaflow.recommendation.application.assembler.RecommendationAssembler;
import com.novaflow.common.domain.event.DomainEvent;
import com.novaflow.recommendation.domain.model.recommendation.Recommendation;
import com.novaflow.recommendation.domain.model.recommendation.exception.RecommendationGenerationFailedException;
import com.novaflow.recommendation.domain.model.recommendation.exception.RecommendationNotFoundException;
import com.novaflow.recommendation.domain.model.recommendation.service.DeduplicationService;
import com.novaflow.recommendation.domain.model.recommendation.service.RecommendationGenerator;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.RecommendationContext;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.RecommendationHistory;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.RecommendationItem;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.SceneAnalysis;
import com.novaflow.common.domain.valueobject.UserId;
import com.novaflow.common.domain.valueobject.VideoId;
import com.novaflow.common.domain.valueobject.RecommendationId;
import com.novaflow.recommendation.infra.external.auth.Video;
import com.novaflow.recommendation.infra.external.auth.VideoRepository;
import com.novaflow.common.domain.repository.DomainEventPublisher;
import com.novaflow.recommendation.domain.repository.RecommendationHistoryRepository;
import com.novaflow.recommendation.domain.repository.RecommendationRepository;
import com.novaflow.recommendation.infra.external.ai.VideoAnalysisService;
import com.novaflow.recommendation.infra.external.map.MapService;
import com.novaflow.recommendation.infra.security.TokenService;
import com.novaflow.recommendation.interfaces.dto.request.SelectionRequest;
import com.novaflow.recommendation.interfaces.dto.response.RecommendationDetailResponse;
import com.novaflow.recommendation.interfaces.dto.response.RecommendationHistoryResponse;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.Location;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

/**
 * 推荐应用服务
 * 协调推荐生成、历史管理和用户反馈的用例编排
 */
@Service
@RequiredArgsConstructor
public class RecommendationApplicationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationApplicationService.class);

    private final RecommendationRepository recommendationRepository;
    private final RecommendationHistoryRepository historyRepository;
    private final VideoRepository videoRepository;
    private final DomainEventPublisher eventPublisher;
    private final RecommendationGenerator recommendationGenerator;
    private final DeduplicationService deduplicationService;
    private final VideoAnalysisService videoAnalysisService;
    private final MapService mapService;
    private final TokenService tokenService;

    /**
     * 生成推荐
     */
    @Transactional
    public RecommendationDetailResponse generateRecommendation(String videoId, String token) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);
        VideoId vid = VideoId.of(videoId);

        // 2. 检查是否已有推荐
        var existingRecommendation = recommendationRepository.findByVideoId(vid);
        if (existingRecommendation.isPresent() && existingRecommendation.get().belongsToUser(userId)) {
            return RecommendationAssembler.toDetailResponse(existingRecommendation.get());
        }

        // 3. 获取视频信息
        Video video = videoRepository.findById(vid)
                .orElseThrow(() -> new RecommendationNotFoundException("视频不存在"));

        // 4. 获取场景分析结果
        var analysisResult = videoAnalysisService.getAnalysisResult(videoId);

        // 5. 构建场景分析
        SceneAnalysis sceneAnalysis = SceneAnalysis.of(
                analysisResult.sceneType(),
                analysisResult.sceneDescription(),
                analysisResult.detectedObjects() != null ? analysisResult.detectedObjects() : List.of(),
                analysisResult.foodItems() != null ? analysisResult.foodItems() : List.of()
        );

        // 6. 构建推荐上下文
        Location location = video.getLocation() != null ? video.getLocation() : Location.empty();
        String timeOfDay = determineTimeOfDay();
        RecommendationContext context = RecommendationContext.of(
                location,
                timeOfDay,
                null, // 天气信息可从天气服务获取
                null  // 用户上下文可从用户偏好获取
        );

        // 7. 获取用户推荐历史
        RecommendationHistory history = historyRepository.findByUserId(userId)
                .orElse(RecommendationHistory.empty());

        try {
            // 8. 生成推荐（核心逻辑）
            Recommendation recommendation = Recommendation.create(
                    userId,
                    vid,
                    sceneAnalysis,
                    context,
                    history,
                    recommendationGenerator,
                    deduplicationService
            );

            // 9. 保存推荐
            recommendation = recommendationRepository.save(recommendation);

            // 10. 更新推荐历史
            for (RecommendationItem item : recommendation.getItems()) {
                history = history.addRecommended(item.getFoodName());
            }
            history = history.trim(50); // 保留最近50条
            historyRepository.save(userId, history);

            // 11. 发布领域事件
            publishDomainEvents(recommendation);

            return RecommendationAssembler.toDetailResponse(recommendation);

        } catch (RecommendationGenerationFailedException e) {
            log.error("推荐生成失败: videoId={}, userId={}", videoId, userIdStr, e);
            throw e;
        }
    }

    /**
     * 获取推荐历史
     */
    public RecommendationHistoryResponse getHistory(String token, int page, int pageSize) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);

        // 2. 查询推荐历史
        List<Recommendation> recommendations = recommendationRepository.findByUserId(userId, page, pageSize);
        long total = recommendationRepository.countByUserId(userId);

        return RecommendationAssembler.toHistoryResponse(recommendations, total);
    }

    /**
     * 获取推荐详情
     */
    public RecommendationDetailResponse getDetail(String recommendationId) {
        RecommendationId id = RecommendationId.of(recommendationId);
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new RecommendationNotFoundException());

        return RecommendationAssembler.toDetailResponse(recommendation);
    }

    /**
     * 记录用户选择
     */
    @Transactional
    public void recordSelection(String token, SelectionRequest request) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);

        // 2. 查找推荐
        RecommendationId recommendationId = RecommendationId.of(request.getRecommendationId());
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RecommendationNotFoundException());

        // 3. 验证权限
        if (!recommendation.belongsToUser(userId)) {
            throw new IllegalArgumentException("无权访问此推荐");
        }

        // 4. 记录点击
        Recommendation.RecommendationItemClick click = recommendation.recordItemClick(
                request.getSelectedFood(),
                request.getSelectedPlace()
        );

        // 5. 更新推荐历史
        RecommendationHistory history = historyRepository.findByUserId(userId)
                .orElse(RecommendationHistory.empty());
        history = history.addSelected(request.getSelectedFood());
        historyRepository.save(userId, history);

        // 6. 发布事件
        publishDomainEvents(recommendation);

        // 7. TODO: 创建UserSelection聚合（在Phase 5中实现）
        log.info("用户选择已记录: userId={}, food={}, place={}", userIdStr, request.getSelectedFood(), request.getSelectedPlace());
    }

    /**
     * 判断当前时间段
     */
    private String determineTimeOfDay() {
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(9, 0))) {
            return "早餐";
        } else if (now.isBefore(LocalTime.of(11, 30))) {
            return "上午茶";
        } else if (now.isBefore(LocalTime.of(14, 0))) {
            return "午餐";
        } else if (now.isBefore(LocalTime.of(17, 0))) {
            return "下午茶";
        } else if (now.isBefore(LocalTime.of(20, 0))) {
            return "晚餐";
        } else {
            return "夜宵";
        }
    }

    /**
     * 发布领域事件
     */
    private void publishDomainEvents(Recommendation recommendation) {
        for (DomainEvent event : recommendation.getDomainEvents()) {
            eventPublisher.publishAsync(event);
        }
        recommendation.clearDomainEvents();
    }
}
