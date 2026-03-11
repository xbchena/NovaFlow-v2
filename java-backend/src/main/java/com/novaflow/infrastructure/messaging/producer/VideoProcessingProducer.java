package com.novaflow.infrastructure.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;

/**
 * 视频处理消息生产者
 * 负责发送视频上传和分析相关的消息到 RocketMQ
 */
@Slf4j
@Service
public class VideoProcessingProducer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VideoProcessingProducer.class);

    @Autowired
    private DefaultMQProducer producer;

    @Value("${rocketmq.topic.video-processing}")
    private String topic;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送视频上传消息到 RocketMQ
     *
     * @param videoId 视频 ID
     * @param userId  用户 ID
     */
    public void sendVideoUploadMessage(Long videoId, Long userId) {
        try {
            VideoUploadMessage message = new VideoUploadMessage(videoId, userId, System.currentTimeMillis());
            String messageBody = objectMapper.writeValueAsString(message);

            Message msg = new Message(
                    topic,
                    "video-upload", // tag
                    "video-upload-" + videoId, // key
                    messageBody.getBytes(StandardCharsets.UTF_8)
            );

            producer.send(msg);
            log.info("视频上传消息已发送到 RocketMQ: videoId={}, userId={}", videoId, userId);
        } catch (Exception e) {
            log.error("发送视频上传消息失败: videoId={}, userId={}, error={}",
                    videoId, userId, e.getMessage(), e);
            throw new RuntimeException("发送视频上传消息失败", e);
        }
    }

    /**
     * 发送视频分析消息到 RocketMQ
     *
     * @param videoId 视频 ID
     * @param userId  用户 ID
     */
    public void sendVideoAnalysisMessage(Long videoId, Long userId) {
        try {
            VideoAnalysisMessage message = new VideoAnalysisMessage(videoId, userId, System.currentTimeMillis());
            String messageBody = objectMapper.writeValueAsString(message);

            Message msg = new Message(
                    topic,
                    "video-analysis", // tag
                    "video-analysis-" + videoId, // key
                    messageBody.getBytes(StandardCharsets.UTF_8)
            );

            producer.send(msg);
            log.info("视频分析消息已发送到 RocketMQ: videoId={}, userId={}", videoId, userId);
        } catch (Exception e) {
            log.error("发送视频分析消息失败: videoId={}, userId={}, error={}",
                    videoId, userId, e.getMessage(), e);
            throw new RuntimeException("发送视频分析消息失败", e);
        }
    }

    /**
     * 视频上传消息
     */
    public record VideoUploadMessage(Long videoId, Long userId, Long timestamp) {}

    /**
     * 视频分析消息
     */
    public record VideoAnalysisMessage(Long videoId, Long userId, Long timestamp) {}
}
