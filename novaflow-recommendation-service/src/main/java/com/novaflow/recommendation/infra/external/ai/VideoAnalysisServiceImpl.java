package com.novaflow.recommendation.infra.external.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 视频分析服务实现
 * 基于 Spring AI 和 DashScope 实现视频内容分析
 */
@Slf4j
@Service
public class VideoAnalysisServiceImpl implements VideoAnalysisService {

    private static final String CHAT_MEMORY_CONVERSATION_ID = "chat_memory_conversation_id";

    private final ChatClient videoAnalysisChatClient;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存储视频分析结果的缓存
    private final Map<String, AnalysisResult> analysisCache = new ConcurrentHashMap<>();

    public VideoAnalysisServiceImpl(
            @Qualifier("videoAnalysisChatClient") ChatClient videoAnalysisChatClient,
            @Lazy ChatClient chatClient) {
        this.videoAnalysisChatClient = videoAnalysisChatClient;
        this.chatClient = chatClient;
    }

    @Override
    public AnalysisResult analyzeVideo(String videoId) {
        log.info("开始分析视频: videoId={}", videoId);

        try {
            // 构建分析提示词
            String analysisPrompt = buildAnalysisPrompt(videoId);

            // 调用 AI 模型进行分析
            ChatResponse response = videoAnalysisChatClient.prompt()
                    .user(analysisPrompt)
                    .call()
                    .chatResponse();

            String result = response.getResult().getOutput().getText();
            log.info("视频分析完成: videoId={}, result={}", videoId, result);

            // 解析分析结果
            AnalysisResult analysisResult = parseAnalysisResult(result);
            analysisCache.put(videoId, analysisResult);

            return analysisResult;

        } catch (Exception e) {
            log.error("视频分析失败: videoId={}, error={}", videoId, e.getMessage(), e);
            throw new RuntimeException("视频分析失败: " + e.getMessage(), e);
        }
    }

    @Override
    public AnalysisResult getAnalysisResult(String videoId) {
        log.info("获取视频分析结果: videoId={}", videoId);

        // 先从缓存中获取
        AnalysisResult cached = analysisCache.get(videoId);
        if (cached != null) {
            log.info("从缓存获取视频分析结果: videoId={}", videoId);
            return cached;
        }

        // 如果缓存中没有，返回默认结果
        log.warn("视频分析结果不存在: videoId={}", videoId);
        return getDefaultAnalysisResult();
    }

    /**
     * 分析视频帧
     * 用于逐帧分析视频内容
     *
     * @param image       视频帧图像
     * @param timestamp   时间戳
     * @param context     上下文信息
     * @return 分析结果
     */
    public String analyzeVideoFrame(MultipartFile image, String timestamp, String context) {
        log.info("分析视频帧: timestamp={}, context={}", timestamp, context);

        try {
            String prompt = String.format(
                    "请分析这个视频帧（时间戳：%s）。%s " +
                    "请描述：\n" +
                    "1. 场景类型（餐厅、家庭、户外等）\n" +
                    "2. 可见的食物类型\n" +
                    "3. 人物数量和状态\n" +
                    "4. 环境氛围",
                    timestamp,
                    context != null ? "上下文信息：" + context : ""
            );

            return videoAnalysisChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("视频帧分析失败: timestamp={}, error={}", timestamp, e.getMessage(), e);
            throw new RuntimeException("视频帧分析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 多轮对话交互
     * 支持上下文的对话功能
     *
     * @param sessionId   会话ID
     * @param userMessage 用户消息
     * @return AI 响应
     */
    public String chat(String sessionId, String userMessage) {
        String conversationId = normalizeSessionId(sessionId);
        log.info("处理对话请求: sessionId={}, message={}", conversationId, userMessage);

        try {
            // 使用带记忆的 ChatClient 进行对话
            return chatClient.prompt()
                    .advisors(advisor -> advisor.param(CHAT_MEMORY_CONVERSATION_ID, conversationId))
                    .user(userMessage)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("对话处理失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
            throw new RuntimeException("对话处理失败: " + e.getMessage(), e);
        }
    }

    private String normalizeSessionId(String sessionId) {
        return sessionId == null || sessionId.isBlank() ? "default" : sessionId;
    }

    /**
     * 清除会话历史
     *
     * @param sessionId 会话ID
     */
    public void clearSessionHistory(String sessionId) {
        log.info("清除会话历史: sessionId={}", sessionId);
        // ChatClient 的 Memory 会自动管理，这里可以扩展为持久化存储的清理
    }

    /**
     * 构建视频分析提示词
     */
    private String buildAnalysisPrompt(String videoId) {
        return String.format(
                "请分析视频 ID: %s 的内容。\n\n" +
                "请提供以下信息：\n" +
                "1. 场景类型：餐厅、家庭、办公室、户外等\n" +
                "2. 场景描述：详细描述视频中的场景和环境\n" +
                "3. 识别的食物：列出视频中可见的所有食物\n" +
                "4. 推荐的食物类型：基于场景推荐合适的食物\n" +
                "5. 适合的就餐地点类型：餐厅类型、外卖等\n\n" +
                "请以 JSON 格式返回结果，包含以下字段：\n" +
                "{\n" +
                "  \"sceneType\": \"场景类型\",\n" +
                "  \"sceneDescription\": \"场景描述\",\n" +
                "  \"detectedObjects\": [\"识别的物体列表\"],\n" +
                "  \"foodItems\": [\"识别的食物列表\"]\n" +
                "}",
                videoId
        );
    }

    /**
     * 解析 AI 返回的分析结果
     */
    private AnalysisResult parseAnalysisResult(String result) {
        try {
            // 尝试从 JSON 格式解析
            if (result.trim().startsWith("{")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> json = objectMapper.readValue(result, Map.class);

                String sceneType = (String) json.getOrDefault("sceneType", "未知");
                String sceneDescription = (String) json.getOrDefault("sceneDescription", "");
                @SuppressWarnings("unchecked")
                List<String> detectedObjects = (List<String>) json.getOrDefault("detectedObjects", List.of());
                @SuppressWarnings("unchecked")
                List<String> foodItems = (List<String>) json.getOrDefault("foodItems", List.of());

                return new AnalysisResult(
                        sceneType,
                        sceneDescription,
                        null, // thumbnailUrl
                        List.of(), // recommendations
                        List.of()  // nearbyPlaces
                );
            }
        } catch (Exception e) {
            log.warn("解析 JSON 格式结果失败，使用默认解析: {}", e.getMessage());
        }

        // 简单文本解析
        return new AnalysisResult(
                "未知",
                result,
                null,
                List.of(),
                List.of()
        );
    }

    /**
     * 获取默认的分析结果
     */
    private AnalysisResult getDefaultAnalysisResult() {
        return new AnalysisResult(
                "未知",
                "分析结果暂不可用",
                null,
                List.of(),
                List.of()
        );
    }
}
