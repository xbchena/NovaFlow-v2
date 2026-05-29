package com.novaflow.feedback.infra.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;

/**
 * 推荐消息生产者
 * 负责发送推荐相关的消息到 RocketMQ
 */
@Service
public class RecommendationProducer {

    private static final Logger log = LoggerFactory.getLogger(RecommendationProducer.class);

    @Autowired
    private DefaultMQProducer producer;

    @Value("${rocketmq.topic.recommendation}")
    private String topic;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送推荐生成消息到 RocketMQ
     *
     * @param recommendationId 推荐 ID
     * @param userId           用户 ID
     * @param videoId          视频 ID
     */
    public void sendRecommendationMessage(Long recommendationId, Long userId, Long videoId) {
        try {
            RecommendationMessage message = new RecommendationMessage(
                    recommendationId, userId, videoId, System.currentTimeMillis()
            );
            String messageBody = objectMapper.writeValueAsString(message);

            Message msg = new Message(
                    topic,
                    "recommendation", // tag
                    "recommendation-" + recommendationId, // key
                    messageBody.getBytes(StandardCharsets.UTF_8)
            );

            producer.send(msg);
            log.info("推荐消息已发送到 RocketMQ: recommendationId={}, userId={}, videoId={}",
                    recommendationId, userId, videoId);
        } catch (Exception e) {
            log.error("发送推荐消息失败: recommendationId={}, userId={}, videoId={}, error={}",
                    recommendationId, userId, videoId, e.getMessage(), e);
            throw new RuntimeException("发送推荐消息失败", e);
        }
    }

    /**
     * 推荐消息
     */
    public record RecommendationMessage(Long recommendationId, Long userId, Long videoId, Long timestamp) {}
}
