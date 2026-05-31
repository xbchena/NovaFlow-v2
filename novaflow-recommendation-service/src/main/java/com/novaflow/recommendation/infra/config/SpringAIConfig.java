package com.novaflow.recommendation.infra.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置类
 * 配置 ChatClient、ChatMemory 等 Spring AI 核心组件
 */
@Configuration
public class SpringAIConfig {

    @Value("${app.ai.tongyi.system-prompt:你是一个智能饮食推荐助手，专门分析视频内容并提供饮食建议。}")
    private String systemPrompt;

    /**
     * 创建带记忆的 ChatClient
     * 用于进行具有上下文的对话交互
     *
     * @param chatModel     聊天模型
     * @param chatMemory    聊天记忆实例
     * @return ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

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
