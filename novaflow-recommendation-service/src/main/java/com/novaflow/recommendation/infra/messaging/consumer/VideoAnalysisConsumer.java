package com.novaflow.recommendation.infra.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaflow.recommendation.infra.messaging.producer.VideoProcessingProducer.VideoAnalysisMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 视频分析消息消费者
 * 处理视频分析相关的消息
 */
@Service
@RocketMQMessageListener(
        topic = "${rocketmq.topic.video-processing}",
        selectorExpression = "video-analysis",
        consumerGroup = "${rocketmq.consumer-group.video-analysis}",
        maxReconsumeTimes = 3
)
public class VideoAnalysisConsumer implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(VideoAnalysisConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(String messageBody) {
        try {
            log.info("开始处理视频分析消息: {}", messageBody);

            VideoAnalysisMessage message = objectMapper.readValue(messageBody, VideoAnalysisMessage.class);

            log.info("解析视频分析消息成功: videoId={}, userId={}",
                    message.videoId(), message.userId());

            // 处理视频分析逻辑
            processVideoAnalysis(message);

            log.info("视频分析消息处理成功: videoId={}", message.videoId());
        } catch (Exception e) {
            log.error("处理视频分析消息失败: messageBody={}, error={}",
                    messageBody, e.getMessage(), e);
            // 抛出异常让消息重试
            throw new RuntimeException("处理视频分析消息失败", e);
        }
    }

    /**
     * 处理视频分析逻辑
     */
    private void processVideoAnalysis(VideoAnalysisMessage message) {
        log.info("执行视频分析处理: videoId={}, userId={}", message.videoId(), message.userId());

        // TODO: 实现具体的视频分析处理逻辑
        // 1. 调用 AI 分析服务
        // 2. 提取场景信息
        // 3. 生成推荐
        // 4. 更新视频状态

        try {
            // 模拟处理时间
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
