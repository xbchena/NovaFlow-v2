package com.novaflow.recommendation.interfaces.rest;

import com.novaflow.recommendation.infra.memory.SceneContextManager;
import com.novaflow.recommendation.infra.memory.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;
    private final ChatClient videoAnalysisChatClient;
    private final ChatMemory chatMemory;
    private final SceneContextManager sceneContextManager;
    private final UserProfileService userProfileService;

    /**
     * 发送聊天消息（核心端点）
     * 接入三层记忆：L3 用户画像 + L2 场景上下文 + L1 短期记忆（Advisor 自动管理）
     */
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        log.info("接收聊天消息: sessionId={}, userId={}, message={}",
                request.getSessionId(), request.getUserId(), request.getMessage());

        try {
            if (request.getVideoId() != null && !request.getVideoId().isBlank()) {
                sceneContextManager.setCurrentScene(
                        request.getSessionId(),
                        SceneContextManager.SceneContext.of(request.getVideoId(), null, null, "推荐")
                );
            }

            String contextPrompt = buildContextPrompt(request.getUserId(), request.getSessionId());

            String response = chatClient.prompt()
                    .system(contextPrompt)
                    .user(request.getMessage())
                    .call()
                    .content();

            return ResponseEntity.ok(ChatResponse.builder()
                    .sessionId(request.getSessionId())
                    .message(response)
                    .timestamp(System.currentTimeMillis())
                    .build());

        } catch (Exception e) {
            log.error("处理聊天消息失败: sessionId={}, error={}",
                    request.getSessionId(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    ChatResponse.builder()
                            .sessionId(request.getSessionId())
                            .message("抱歉，处理消息时出错，请稍后重试。")
                            .timestamp(System.currentTimeMillis())
                            .build()
            );
        }
    }

    @PostMapping(value = "/analyze-frame", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FrameAnalysisResponse> analyzeFrame(
            @RequestParam("image") MultipartFile image,
            @RequestParam("timestamp") String timestamp,
            @RequestParam(value = "sessionId", required = false, defaultValue = "default") String sessionId,
            @RequestParam(value = "context", required = false) String context) {

        log.info("分析视频帧: sessionId={}, timestamp={}", sessionId, timestamp);

        try {
            String prompt = String.format(
                    "请分析这个视频帧（时间戳：%s）。%s\n请描述：\n1. 场景类型\n2. 可见的食物\n3. 人物数量和状态\n4. 环境氛围",
                    timestamp,
                    context != null ? "上下文：" + context : ""
            );

            String analysis = videoAnalysisChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            sceneContextManager.setCurrentScene(
                    sessionId,
                    SceneContextManager.SceneContext.of(null, "视频帧分析", List.of())
            );

            return ResponseEntity.ok(FrameAnalysisResponse.builder()
                    .sessionId(sessionId)
                    .timestamp(timestamp)
                    .analysis(analysis)
                    .build());

        } catch (Exception e) {
            log.error("视频帧分析失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    FrameAnalysisResponse.builder()
                            .sessionId(sessionId)
                            .timestamp(timestamp)
                            .analysis("分析失败: " + e.getMessage())
                            .build()
            );
        }
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        log.info("清除会话历史: sessionId={}", sessionId);
        chatMemory.clear(sessionId);
        sceneContextManager.clearScene(sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scene/{sessionId}")
    public ResponseEntity<Map<String, Object>> getScene(@PathVariable String sessionId) {
        var sceneOpt = sceneContextManager.getCurrentScene(sessionId);
        Map<String, Object> result = sceneOpt
                .map(scene -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sessionId", sessionId);
                    map.put("videoId", scene.videoId());
                    map.put("sceneType", scene.sceneType());
                    map.put("detectedFood", scene.detectedFood());
                    map.put("userIntent", scene.userIntent());
                    map.put("createdAt", scene.createdAt());
                    return map;
                })
                .orElseGet(() -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sessionId", sessionId);
                    map.put("active", false);
                    return map;
                });
        return ResponseEntity.ok(result);
    }

    private String buildContextPrompt(Long userId, String sessionId) {
        StringBuilder prompt = new StringBuilder();
        if (userId != null) {
            String profilePrompt = userProfileService.buildProfilePrompt(userId);
            if (!profilePrompt.isEmpty()) {
                prompt.append(profilePrompt).append("\n");
            }
        }
        String scenePrompt = sceneContextManager.buildScenePrompt(sessionId);
        if (!scenePrompt.isEmpty()) {
            prompt.append(scenePrompt).append("\n");
        }
        return prompt.toString();
    }

    // ---- DTO ----

    public static class ChatRequest {
        private String sessionId = "default";
        private String message;
        private Long userId;
        private String videoId;

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getVideoId() { return videoId; }
        public void setVideoId(String videoId) { this.videoId = videoId; }
    }

    @lombok.Builder
    @lombok.Data
    public static class ChatResponse {
        private String sessionId;
        private String message;
        private Long timestamp;
    }

    @lombok.Builder
    @lombok.Data
    public static class FrameAnalysisResponse {
        private String sessionId;
        private String timestamp;
        private String analysis;
    }
}
