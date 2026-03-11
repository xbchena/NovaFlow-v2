package com.novaflow.infrastructure.messaging.deadletter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列处理器
 * 处理从业务队列转发过来的失败消息
 */
@Slf4j
@Component
public class DeadLetterQueueProcessor {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterQueueProcessor.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理视频上传死信队列消息
     */
    @RabbitListener(queues = "video.upload.dlq")
    public void processVideoUploadDLQ(Message message) {
        handleDeadLetterMessage("视频上传", message);
    }

    /**
     * 处理视频分析死信队列消息
     */
    @RabbitListener(queues = "video.analysis.dlq")
    public void processVideoAnalysisDLQ(Message message) {
        handleDeadLetterMessage("视频分析", message);
    }

    /**
     * 处理推荐死信队列消息
     */
    @RabbitListener(queues = "recommendation.dlq")
    public void processRecommendationDLQ(Message message) {
        handleDeadLetterMessage("推荐", message);
    }

    /**
     * 统一处理死信队列消息
     */
    private void handleDeadLetterMessage(String queueName, Message message) {
        try {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);

            // 记录死信消息详细信息
            Map<String, Object> deathInfo = extractDeathInfo(message);
            log.error("========== 死信消息 ==========");
            log.error("队列: {}", queueName);
            log.error("消息ID: {}", message.getMessageProperties().getMessageId());
            log.error("消息内容: {}", messageBody);
            log.error("死信原因: {}", deathInfo.get("reason"));
            log.error("原始交换机: {}", deathInfo.get("originalExchange"));
            log.error("原始路由键: {}", deathInfo.get("originalRoutingKey"));
            log.error("死亡时间: {}", deathInfo.get("deathTime"));
            log.error("重试次数: {}", deathInfo.get("retryCount"));
            log.error("==============================");

            // 根据不同的死信原因采取不同的处理策略
            String reason = (String) deathInfo.get("reason");
            switch (reason) {
                case "expired":
                    // 消息过期，可能是处理太慢
                    handleExpiredMessage(queueName, messageBody);
                    break;
                case "rejected":
                    // 消息被拒绝，可能是业务逻辑处理失败
                    handleRejectedMessage(queueName, messageBody);
                    break;
                default:
                    // 其他原因
                    handleOtherDLQMessage(queueName, messageBody, deathInfo);
            }

        } catch (Exception e) {
            log.error("处理死信队列消息时发生错误: queueName={}, error={}", queueName, e.getMessage(), e);
        }
    }

    /**
     * 提取死信消息的详细信息
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractDeathInfo(Message message) {
        Map<String, Object> info = new HashMap<>();

        // 提取x-death头信息
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        if (headers.containsKey("x-death")) {
            Object deathHeader = headers.get("x-death");
            if (deathHeader instanceof java.util.List) {
                java.util.List<Map<String, Object>> deathList = (java.util.List<Map<String, Object>>) deathHeader;
                if (!deathList.isEmpty()) {
                    Map<String, Object> deathEntry = deathList.get(0);
                    info.put("reason", deathEntry.get("reason"));
                    info.put("originalExchange", deathEntry.get("exchange"));
                    info.put("originalRoutingKey", deathEntry.get("routing-keys"));
                    info.put("retryCount", deathList.size());
                    info.put("deathTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
            }
        }

        // 设置默认值
        if (!info.containsKey("reason")) {
            info.put("reason", "unknown");
        }
        if (!info.containsKey("retryCount")) {
            info.put("retryCount", 0);
        }

        return info;
    }

    /**
     * 处理过期消息
     * 通常是因为消费者处理速度慢导致消息超时
     */
    private void handleExpiredMessage(String queueName, String messageBody) {
        log.warn("消息已过期，可能需要优化消费者处理性能: queue={}", queueName);

        // 策略1: 记录到数据库用于后续分析
        // saveToDLQRecord(queueName, messageBody, "expired");

        // 策略2: 可以选择重新入队（需要业务判断）
        // republishToOriginalQueue(queueName, messageBody);

        // 策略3: 发送告警通知
        // sendAlert(queueName, "消息过期", messageBody);
    }

    /**
     * 处理被拒绝的消息
     * 通常是因为业务逻辑处理失败
     */
    private void handleRejectedMessage(String queueName, String messageBody) {
        log.warn("消息被拒绝，可能存在业务逻辑问题: queue={}", queueName);

        // 分析消息内容，判断是否可以修复后重试
        // 如果是数据格式问题，可以尝试修复后重新发送
        // 如果是临时性错误，可以延迟重试
    }

    /**
     * 处理其他类型的死信消息
     */
    private void handleOtherDLQMessage(String queueName, String messageBody, Map<String, Object> deathInfo) {
        log.error("未知的死信原因: queue={}, info={}", queueName, deathInfo);

        // 根据实际情况决定是否需要人工介入处理
    }
}
