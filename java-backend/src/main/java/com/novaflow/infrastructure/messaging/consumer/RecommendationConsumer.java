package com.novaflow.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaflow.infrastructure.messaging.producer.RecommendationProducer.RecommendationMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 推荐消息消费者
 * 处理推荐相关的消息
 */
@Service
@RocketMQMessageListener(
        topic = "${rocketmq.topic.recommendation}",
        selectorExpression = "recommendation",
        consumerGroup = "${rocketmq.consumer-group.recommendation}",
        maxReconsumeTimes = 3
)
public class RecommendationConsumer implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(RecommendationConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(String messageBody) {
        try {
            log.info("开始处理推荐消息: {}", messageBody);

            RecommendationMessage message = objectMapper.readValue(messageBody, RecommendationMessage.class);

            log.info("解析推荐消息成功: recommendationId={}, userId={}, videoId={}",
                    message.recommendationId(), message.userId(), message.videoId());

            // 处理推荐逻辑
            processRecommendation(message);

            log.info("推荐消息处理成功: recommendationId={}", message.recommendationId());
        } catch (Exception e) {
            log.error("处理推荐消息失败: messageBody={}, error={}",
                    messageBody, e.getMessage(), e);
            // 抛出异常让消息重试
            throw new RuntimeException("处理推荐消息失败", e);
        }
    }

    /**
     * 处理推荐逻辑
     */
    private void processRecommendation(RecommendationMessage message) {
        log.info("执行推荐处理: recommendationId={}, userId={}, videoId={}",
                message.recommendationId(), message.userId(), message.videoId());

        // TODO: 实现具体的推荐处理逻辑
        // 1. 查询视频分析结果
        // 2. 根据场景生成推荐
        // 3. 调用地图服务获取附近地点
        // 4. 保存推荐结果
        // 5. 发送通知给用户

        try {
            // 模拟处理时间
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
