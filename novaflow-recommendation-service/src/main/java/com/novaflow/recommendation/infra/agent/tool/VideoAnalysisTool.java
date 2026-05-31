package com.novaflow.recommendation.infra.agent.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaflow.recommendation.infra.external.ai.VideoAnalysisService;
import com.novaflow.recommendation.infra.memory.SceneContextManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 视频分析工具
 *
 * <p>调用 VideoAnalysisService 分析视频内容，识别场景类型和食物，
 * 并将分析结果更新到 L2 场景记忆（SceneContextManager）中。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VideoAnalysisTool {

    private final VideoAnalysisService videoAnalysisService;
    private final SceneContextManager sceneContextManager;
    private final ObjectMapper objectMapper;

    @Tool(description = "分析视频内容，识别视频中的场景类型（如餐厅、街边、家庭等）和出现的食物。"
            + "分析结果会自动更新到场景记忆中，供后续推荐使用。"
            + "当用户分享或提及视频时调用此工具。")
    public String analyzeVideo(
            @ToolParam(description = "要分析的视频ID") String videoId,
            @ToolParam(description = "当前会话ID，用于关联场景记忆", required = false) String sessionId) {

        log.info("执行视频分析工具: videoId={}, sessionId={}", videoId, sessionId);

        try {
            VideoAnalysisService.AnalysisResult result = videoAnalysisService.analyzeVideo(videoId);

            // 更新 L2 场景记忆
            if (sessionId != null && !sessionId.isBlank()) {
                var foodNames = result.getRecommendations().stream()
                        .map(r -> r.getFoodName())
                        .toList();
                sceneContextManager.setCurrentScene(
                        sessionId,
                        SceneContextManager.SceneContext.of(
                                videoId,
                                result.getSceneType(),
                                foodNames
                        )
                );
                log.info("场景记忆已更新: sessionId={}, sceneType={}", sessionId, result.getSceneType());
            }

            // 构建返回 JSON
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("videoId", videoId);
            response.put("sceneType", result.getSceneType());
            response.put("sceneDescription", result.getSceneDescription());

            var foodRecs = result.getRecommendations().stream()
                    .map(r -> Map.of(
                            "foodName", r.getFoodName(),
                            "foodType", r.getFoodType(),
                            "reason", r.getReason() != null ? r.getReason() : "",
                            "priceHint", r.getPriceHint() != null ? r.getPriceHint() : "",
                            "matchScore", r.getMatchScore() != null ? r.getMatchScore() : 0
                    ))
                    .collect(Collectors.toList());
            response.put("foodRecommendations", foodRecs);

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            log.error("视频分析失败: videoId={}", videoId, e);
            try {
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("success", false);
                error.put("videoId", videoId);
                error.put("error", "视频分析失败: " + e.getMessage());
                return objectMapper.writeValueAsString(error);
            } catch (JsonProcessingException ex) {
                return "{\"success\":false,\"error\":\"视频分析失败\"}";
            }
        }
    }
}
