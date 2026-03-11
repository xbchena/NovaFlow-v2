package com.novaflow.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaflow.infrastructure.messaging.producer.VideoProcessingProducer.VideoUploadMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 视频上传消息消费者
 * 处理视频上传相关的消息
 */
@Service
@RocketMQMessageListener(
        topic = "${rocketmq.topic.video-processing}",
        selectorExpression = "video-upload",
        consumerGroup = "${rocketmq.consumer-group.video-upload}",
        maxReconsumeTimes = 3
)
public class VideoUploadConsumer implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(VideoUploadConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(String messageBody) {
        try {
            log.info("开始处理视频上传消息: {}", messageBody);

            VideoUploadMessage message = objectMapper.readValue(messageBody, VideoUploadMessage.class);

            log.info("解析视频上传消息成功: videoId={}, userId={}",
                    message.videoId(), message.userId());

            // 处理视频上传逻辑
            processVideoUpload(message);

            log.info("视频上传消息处理成功: videoId={}", message.videoId());
        } catch (Exception e) {
            log.error("处理视频上传消息失败: messageBody={}, error={}",
                    messageBody, e.getMessage(), e);
            // 抛出异常让消息重试
            throw new RuntimeException("处理视频上传消息失败", e);
        }
    }

    /**
     * 处理视频上传逻辑
     */
    private void processVideoUpload(VideoUploadMessage message) {
        log.info("执行视频上传处理: videoId={}, userId={}", message.videoId(), message.userId());

        // TODO: 实现具体的视频上传处理逻辑
        // 1. 验证视频文件
        // 2. 上传到 OSS
        // 3. 更新视频状态
        // 4. 发送视频分析消息

        try {
            // 模拟处理时间
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
