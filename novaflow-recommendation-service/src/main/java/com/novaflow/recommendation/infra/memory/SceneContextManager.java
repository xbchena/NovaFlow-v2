package com.novaflow.recommendation.infra.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 场景上下文管理器 - L2 场景记忆
 * 管理用户在对话过程中的实时场景上下文（当前观看的视频、识别的食物等）
 * 存储在 Redis 中，TTL 2 小时自动过期
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SceneContextManager {

    private static final String KEY_PREFIX = "scene:ctx:";
    private static final Duration TTL = Duration.ofHours(2);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void setCurrentScene(String sessionId, SceneContext context) {
        String key = buildKey(sessionId);
        try {
            String json = objectMapper.writeValueAsString(context);
            redisTemplate.opsForValue().set(key, json, TTL);
            log.debug("设置场景上下文: sessionId={}, videoId={}, sceneType={}",
                    sessionId, context.videoId(), context.sceneType());
        } catch (JsonProcessingException e) {
            log.error("序列化场景上下文失败: sessionId={}", sessionId, e);
        }
    }

    public Optional<SceneContext> getCurrentScene(String sessionId) {
        String key = buildKey(sessionId);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        try {
            SceneContext context = objectMapper.readValue(json, SceneContext.class);
            return Optional.of(context);
        } catch (JsonProcessingException e) {
            log.error("反序列化场景上下文失败: sessionId={}", sessionId, e);
            return Optional.empty();
        }
    }

    public void clearScene(String sessionId) {
        String key = buildKey(sessionId);
        redisTemplate.delete(key);
        log.debug("清除场景上下文: sessionId={}", sessionId);
    }

    public String buildScenePrompt(String sessionId) {
        return getCurrentScene(sessionId)
                .map(ctx -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("## 当前场景\n");
                    if (ctx.videoId() != null) {
                        sb.append("- 正在观看视频: ").append(ctx.videoId()).append("\n");
                    }
                    if (ctx.sceneType() != null) {
                        sb.append("- 场景类型: ").append(ctx.sceneType()).append("\n");
                    }
                    if (ctx.detectedFood() != null && !ctx.detectedFood().isEmpty()) {
                        sb.append("- 识别到的食物: ").append(String.join("、", ctx.detectedFood())).append("\n");
                    }
                    if (ctx.userIntent() != null) {
                        sb.append("- 用户意图: ").append(ctx.userIntent()).append("\n");
                    }
                    return sb.toString();
                })
                .orElse("");
    }

    private String buildKey(String sessionId) {
        return KEY_PREFIX + sessionId;
    }

    /**
     * 场景上下文值对象
     */
    public record SceneContext(
            String videoId,
            String sceneType,
            List<String> detectedFood,
            String userIntent,
            LocalDateTime createdAt
    ) {
        public static SceneContext of(String videoId, String sceneType, List<String> detectedFood) {
            return new SceneContext(videoId, sceneType, detectedFood, null, LocalDateTime.now());
        }

        public static SceneContext of(String videoId, String sceneType, List<String> detectedFood, String userIntent) {
            return new SceneContext(videoId, sceneType, detectedFood, userIntent, LocalDateTime.now());
        }
    }
}
