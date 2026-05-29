package com.novaflow.recommendation.infra.messaging.producer;

/**
 * 视频处理消息生产者（本地存根）
 * 来自 video 服务的跨服务依赖，仅提供消息 record 定义
 */
public class VideoProcessingProducer {

    /**
     * 视频分析消息
     */
    public record VideoAnalysisMessage(Long videoId, Long userId, String videoUrl, Long timestamp) {}
}
