package com.novaflow.recommendation.infra.agent;

import com.novaflow.recommendation.infra.agent.tool.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * AI Agent 配置类
 *
 * <p>构建基于 ChatClient + Tool Calling 的 Agent，具备 ReAct 推理能力：
 * <ul>
 *   <li>系统提示词定义 Agent 角色和可用工具</li>
 *   <li>4 个工具：视频分析、用户历史查询、餐厅搜索、推荐生成</li>
 *   <li>ChatMemoryAdvisor 提供 L1 对话记忆</li>
 *   <li>Tool 方法内部会访问 L2 场景记忆和 L3 用户画像</li>
 * </ul>
 */
@Configuration
public class AgentConfig {

    private static final String SYSTEM_PROMPT = """
            你是 NovaFlow 智能美食推荐助手。你的职责是根据用户正在观看的视频内容和历史偏好，\
            为用户推荐合适的美食和附近餐厅。

            ## 工作流程
            1. 如果用户分享了视频，先调用 videoAnalysisTool 分析视频内容，识别场景和食物
            2. 查询用户的饮食偏好和历史推荐记录（调用 userHistoryQueryTool）
            3. 根据场景分析结果和用户偏好，生成个性化美食推荐（调用 recommendationGeneratorTool）
            4. 如果用户需要附近餐厅信息，调用 restaurantSearchTool 搜索周边餐厅
            5. 综合以上信息，给用户提供温暖、个性化的推荐

            ## 注意事项
            - 尊重用户的过敏原和饮食禁忌
            - 推荐理由要具体、有吸引力
            - 如果用户是第一次使用，主动询问偏好
            - 保持友好、热情的语气，像一个懂美食的朋友
            - 回复使用中文
            """;

    @Bean
    @Primary
    public ChatClient agentChatClient(
            ChatModel chatModel,
            ChatMemory chatMemory,
            VideoAnalysisTool videoAnalysisTool,
            UserHistoryQueryTool userHistoryQueryTool,
            RestaurantSearchTool restaurantSearchTool,
            RecommendationGeneratorTool recommendationGeneratorTool) {

        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();

        return ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(memoryAdvisor)
                .defaultTools(
                        videoAnalysisTool,
                        userHistoryQueryTool,
                        restaurantSearchTool,
                        recommendationGeneratorTool
                )
                .build();
    }
}
