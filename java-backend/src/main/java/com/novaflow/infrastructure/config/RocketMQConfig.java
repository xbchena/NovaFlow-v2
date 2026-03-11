package com.novaflow.infrastructure.config;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 配置类
 * 配置生产者和消费者相关的设置
 */
@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Value("${rocketmq.producer.send-message-timeout}")
    private int sendMessageTimeout;

    @Value("${rocketmq.producer.retry-times-when-send-failed}")
    private int retryTimesWhenSendFailed;

    @Value("${rocketmq.producer.max-message-size}")
    private int maxMessageSize;

    /**
     * 配置 RocketMQ 生产者
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(sendMessageTimeout);
        producer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        producer.setMaxMessageSize(maxMessageSize);
        producer.setCompressMsgBodyOverHowmuch(4096);

        // 注意：setEnableMsgTrace 方法在某些版本中可能不可用
        // 如果需要消息轨迹功能，可以添加以下依赖并配置：
        // <dependency>
        //     <groupId>org.apache.rocketmq</groupId>
        //     <artifactId>rocketmq-tool</artifactId>
        //     <version>${rocketmq.version}</version>
        // </dependency>

        return producer;
    }
}
