package com.novaflow.recommendation.infra.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置类
 * 配置视频分析专用 ChatClient（无记忆）
 * 主 ChatClient（带记忆 + 工具）由 AgentConfig 提供（@Primary）
 */
@Configuration
public class SpringAIConfig {

    /**
     * 创建用于视频分析的专用 ChatClient
     * 不使用聊天记忆，每次调用都是独立的
     *
     * @param chatModel     聊天模型
     * @return ChatClient 实例
     */
    @Bean
    public ChatClient videoAnalysisChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你是一个专业的视频分析助手，能够理解和分析视频内容，识别食物、场景和相关信息。")
                .build();
    }
}
