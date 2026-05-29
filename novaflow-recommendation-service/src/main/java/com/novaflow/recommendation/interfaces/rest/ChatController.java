package com.novaflow.recommendation.interfaces.rest;

import com.novaflow.recommendation.infra.external.ai.VideoAnalysisServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 聊天控制器
 * 处理与大模型的对话交互
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final VideoAnalysisServiceImpl videoAnalysisService;

    /**
     * 发送聊天消息
     * 支持多轮对话和上下文记忆
     *
     * @param request   聊天请求
     * @return AI 响应
     */
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        log.info("接收聊天消息: sessionId={}, message={}",
                request.getSessionId(), request.getMessage());

        try {
            String response = videoAnalysisService.chat(
                    request.getSessionId(),
                    request.getMessage()
            );

            return ResponseEntity.ok(ChatResponse.builder()
                    .sessionId(request.getSessionId())
                    .message(response)
                    .timestamp(System.currentTimeMillis())
                    .build());

        } catch (Exception e) {
            log.error("处理聊天消息失败: sessionId={}, error={}",
                    request.getSessionId(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    ChatResponse.builder()
                            .sessionId(request.getSessionId())
                            .message("抱歉，处理消息时出错: " + e.getMessage())
                            .timestamp(System.currentTimeMillis())
                            .build()
            );
        }
    }

    /**
     * 分析视频帧
     *
     * @param image       视频帧图像
     * @param timestamp   时间戳
     * @param sessionId   会话ID（可选）
     * @param context     上下文信息（可选）
     * @return 分析结果
     */
    @PostMapping(value = "/analyze-frame", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FrameAnalysisResponse> analyzeFrame(
            @RequestParam("image") MultipartFile image,
            @RequestParam("timestamp") String timestamp,
            @RequestParam(value = "sessionId", required = false, defaultValue = "default") String sessionId,
            @RequestParam(value = "context", required = false) String context) {

        log.info("分析视频帧: sessionId={}, timestamp={}", sessionId, timestamp);

        try {
            String analysis = videoAnalysisService.analyzeVideoFrame(image, timestamp, context);

            return ResponseEntity.ok(FrameAnalysisResponse.builder()
                    .sessionId(sessionId)
                    .timestamp(timestamp)
                    .analysis(analysis)
                    .build());

        } catch (Exception e) {
            log.error("视频帧分析失败: sessionId={}, error={}",
                    sessionId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    FrameAnalysisResponse.builder()
                            .sessionId(sessionId)
                            .timestamp(timestamp)
                            .analysis("分析失败: " + e.getMessage())
                            .build()
            );
        }
    }

    /**
     * 清除会话历史
     *
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        log.info("清除会话历史: sessionId={}", sessionId);
        videoAnalysisService.clearSessionHistory(sessionId);
        return ResponseEntity.ok().build();
    }

    /**
     * 聊天请求
     */
    public static class ChatRequest {
        private String sessionId = "default";
        private String message;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * 聊天响应
     */
    @lombok.Builder
    @lombok.Data
    public static class ChatResponse {
        private String sessionId;
        private String message;
        private Long timestamp;
    }

    /**
     * 视频帧分析响应
     */
    @lombok.Builder
    @lombok.Data
    public static class FrameAnalysisResponse {
        private String sessionId;
        private String timestamp;
        private String analysis;
    }
}
