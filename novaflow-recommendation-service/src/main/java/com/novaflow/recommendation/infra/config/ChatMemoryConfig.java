package com.novaflow.recommendation.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天记忆配置类
 * 支持基于 Redis 的持久化聊天记忆
 */
@Configuration
public class ChatMemoryConfig {

    private static final Logger log = LoggerFactory.getLogger(ChatMemoryConfig.class);

    @Value("${app.ai.chat.memory.max-messages:50}")
    private int maxMessages;

    @Value("${app.ai.chat.memory.ttl-hours:24}")
    private int ttlHours;

    /**
     * 创建基于滑动窗口的聊天记忆
     */
    @Bean
    @Primary
    public ChatMemory chatMemory(RedisTemplate<String, Object> redisTemplate) {
        return new RedisBackedChatMemory(redisTemplate, maxMessages, ttlHours);
    }

    /**
     * 基于 Redis 的聊天记忆实现
     * 支持多会话管理和持久化存储
     */
    @Component
    public static class RedisBackedChatMemory implements ChatMemory {

        private final RedisTemplate<String, Object> redisTemplate;
        private final int maxMessages;
        private final int ttlHours;
        private final String KEY_PREFIX = "chat:memory:";

        // 内存缓存，提升访问性能
        private final ConcurrentHashMap<String, List<org.springframework.ai.chat.messages.Message>> memoryCache =
                new ConcurrentHashMap<>();

        public RedisBackedChatMemory(RedisTemplate<String, Object> redisTemplate,
                                    int maxMessages, int ttlHours) {
            this.redisTemplate = redisTemplate;
            this.maxMessages = maxMessages;
            this.ttlHours = ttlHours;
        }

        @Override
        public void add(String conversationId, List<org.springframework.ai.chat.messages.Message> messages) {
            String key = buildKey(conversationId);

            // 获取现有消息
            List<org.springframework.ai.chat.messages.Message> existingMessages = get(conversationId);

            // 添加新消息
            existingMessages.addAll(messages);

            // 限制消息数量
            if (existingMessages.size() > maxMessages) {
                existingMessages = existingMessages.subList(
                        existingMessages.size() - maxMessages,
                        existingMessages.size()
                );
            }

            // 保存到 Redis
            redisTemplate.opsForValue().set(key, existingMessages, Duration.ofHours(ttlHours));

            // 更新内存缓存
            memoryCache.put(conversationId, existingMessages);

            log.debug("添加聊天记录: conversationId={}, messages={}", conversationId, messages.size());
        }

        @Override
        public void add(String conversationId, org.springframework.ai.chat.messages.Message message) {
            add(conversationId, List.of(message));
        }

        @Override
        public List<org.springframework.ai.chat.messages.Message> get(String conversationId) {
            String key = buildKey(conversationId);

            // 先从内存缓存获取
            List<org.springframework.ai.chat.messages.Message> cached = memoryCache.get(conversationId);
            if (cached != null) {
                return cached;
            }

            // 从 Redis 获取
            @SuppressWarnings("unchecked")
            List<org.springframework.ai.chat.messages.Message> messages =
                    (List<org.springframework.ai.chat.messages.Message>) redisTemplate.opsForValue().get(key);

            if (messages != null) {
                memoryCache.put(conversationId, messages);
                return messages;
            }

            return List.of();
        }

        @Override
        public void clear(String conversationId) {
            String key = buildKey(conversationId);
            redisTemplate.delete(key);
            memoryCache.remove(conversationId);
            log.debug("清除聊天记录: conversationId={}", conversationId);
        }

        /**
         * 清除所有聊天记录
         */
        public void clearAll() {
            redisTemplate.delete(redisTemplate.keys(KEY_PREFIX + "*"));
            memoryCache.clear();
            log.debug("清除所有聊天记录");
        }

        /**
         * 获取会话数量
         */
        public long getConversationCount() {
            Long count = (long) redisTemplate.keys(KEY_PREFIX + "*").size();
            return count != null ? count : 0;
        }

        private String buildKey(String conversationId) {
            return KEY_PREFIX + conversationId;
        }
    }

    /**
     * 简单的内存聊天记忆实现
     * 用于开发测试或简单场景
     */
    @Component("simpleChatMemory")
    public static class SimpleChatMemory {

        private final ChatMemory memory;

        public SimpleChatMemory() {
            this.memory = MessageWindowChatMemory.builder()
                    .maxMessages(20)
                    .build();
        }

        public ChatMemory getMemory() {
            return memory;
        }
    }
}
