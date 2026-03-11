package com.novaflow.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置类
 * 配置消息队列、交换机和死信队列处理机制
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    // ==================== 交换机定义 ====================

    public static final String VIDEO_PROCESSING_EXCHANGE = "video.processing.exchange";
    public static final String RECOMMENDATION_EXCHANGE = "recommendation.exchange";
    public static final String DLX_EXCHANGE = "dlx.exchange"; // 死信交换机

    // ==================== 队列定义 ====================

    public static final String VIDEO_UPLOAD_QUEUE = "video.upload.queue";
    public static final String VIDEO_ANALYSIS_QUEUE = "video.analysis.queue";
    public static final String RECOMMENDATION_QUEUE = "recommendation.queue";

    // ==================== 死信队列定义 ====================

    public static final String VIDEO_UPLOAD_DLQ = "video.upload.dlq";
    public static final String VIDEO_ANALYSIS_DLQ = "video.analysis.dlq";
    public static final String RECOMMENDATION_DLQ = "recommendation.dlq";

    // ==================== 路由键定义 ====================

    public static final String VIDEO_UPLOAD_ROUTING_KEY = "video.upload";
    public static final String VIDEO_ANALYSIS_ROUTING_KEY = "video.analysis";
    public static final String RECOMMENDATION_ROUTING_KEY = "recommendation";

    /**
     * 死信交换机
     * 用于接收处理失败的消息
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    /**
     * 视频处理业务交换机
     */
    @Bean
    public DirectExchange videoProcessingExchange() {
        return new DirectExchange(VIDEO_PROCESSING_EXCHANGE, true, false);
    }

    /**
     * 推荐业务交换机
     */
    @Bean
    public DirectExchange recommendationExchange() {
        return new DirectExchange(RECOMMENDATION_EXCHANGE, true, false);
    }

    // ==================== 死信队列配置 ====================

    /**
     * 视频上传死信队列
     */
    @Bean
    public Queue videoUploadDeadLetterQueue() {
        return QueueBuilder.durable(VIDEO_UPLOAD_DLQ).build();
    }

    /**
     * 视频分析死信队列
     */
    @Bean
    public Queue videoAnalysisDeadLetterQueue() {
        return QueueBuilder.durable(VIDEO_ANALYSIS_DLQ).build();
    }

    /**
     * 推荐死信队列
     */
    @Bean
    public Queue recommendationDeadLetterQueue() {
        return QueueBuilder.durable(RECOMMENDATION_DLQ).build();
    }

    // ==================== 绑定死信队列到死信交换机 ====================

    /**
     * 绑定视频上传死信队列
     */
    @Bean
    public Binding videoUploadDLQBinding() {
        return BindingBuilder.bind(videoUploadDeadLetterQueue())
                .to(deadLetterExchange())
                .with(VIDEO_UPLOAD_ROUTING_KEY);
    }

    /**
     * 绑定视频分析死信队列
     */
    @Bean
    public Binding videoAnalysisDLQBinding() {
        return BindingBuilder.bind(videoAnalysisDeadLetterQueue())
                .to(deadLetterExchange())
                .with(VIDEO_ANALYSIS_ROUTING_KEY);
    }

    /**
     * 绑定推荐死信队列
     */
    @Bean
    public Binding recommendationDLQBinding() {
        return BindingBuilder.bind(recommendationDeadLetterQueue())
                .to(deadLetterExchange())
                .with(RECOMMENDATION_ROUTING_KEY);
    }

    // ==================== 业务队列配置（带死信路由） ====================

    /**
     * 视频上传队列
     * 配置死信队列，当消息处理失败时自动转发到死信队列
     */
    @Bean
    public Queue videoUploadQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", VIDEO_UPLOAD_ROUTING_KEY);
        args.put("x-message-ttl", 300000); // 5分钟未处理则过期进入死信队列
        return QueueBuilder.durable(VIDEO_UPLOAD_QUEUE)
                .withArguments(args)
                .build();
    }

    /**
     * 视频分析队列
     * 配置死信队列
     */
    @Bean
    public Queue videoAnalysisQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", VIDEO_ANALYSIS_ROUTING_KEY);
        args.put("x-message-ttl", 600000); // 10分钟未处理则过期
        return QueueBuilder.durable(VIDEO_ANALYSIS_QUEUE)
                .withArguments(args)
                .build();
    }

    /**
     * 推荐队列
     * 配置死信队列
     */
    @Bean
    public Queue recommendationQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", RECOMMENDATION_ROUTING_KEY);
        args.put("x-message-ttl", 300000); // 5分钟未处理则过期
        return QueueBuilder.durable(RECOMMENDATION_QUEUE)
                .withArguments(args)
                .build();
    }

    // ==================== 绑定业务队列到交换机 ====================

    @Bean
    public Binding videoUploadBinding() {
        return BindingBuilder.bind(videoUploadQueue())
                .to(videoProcessingExchange())
                .with(VIDEO_UPLOAD_ROUTING_KEY);
    }

    @Bean
    public Binding videoAnalysisBinding() {
        return BindingBuilder.bind(videoAnalysisQueue())
                .to(videoProcessingExchange())
                .with(VIDEO_ANALYSIS_ROUTING_KEY);
    }

    @Bean
    public Binding recommendationBinding() {
        return BindingBuilder.bind(recommendationQueue())
                .to(recommendationExchange())
                .with(RECOMMENDATION_ROUTING_KEY);
    }

    // ==================== 消息转换器 ====================

    /**
     * JSON消息转换器
     * 用于序列化和反序列化消息对象
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置RabbitTemplate
     * 设置消息转换器和确认机制
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        // 开启发送确认
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Message sent successfully to RabbitMQ");
            } else {
                log.error("Failed to send message to RabbitMQ: {}", cause);
            }
        });

        // 开启返回确认
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("Message returned from RabbitMQ: exchange={}, routingKey={}, replyCode={}, replyText={}",
                    returned.getExchange(), returned.getRoutingKey(),
                    returned.getReplyCode(), returned.getReplyText());
        });

        return rabbitTemplate;
    }
}
