# RocketMQ 使用指南

## 概述

本项目已集成 Apache RocketMQ 消息队列，用于处理异步任务和解耦系统组件。

## 配置说明

### 1. 依赖配置
```xml
<!-- Apache RocketMQ Spring Boot Starter -->
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.3.1</version>
</dependency>

<!-- RocketMQ Client -->
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>5.3.1</version>
</dependency>
```

### 2. 连接配置 (application.yml)
```yaml
rocketmq:
  name-server: localhost:9876
  producer:
    group: novaflow-producer-group
    send-message-timeout: 3000
    retry-times-when-send-failed: 2
    max-message-size: 4194304  # 4MB
  consumer:
    group: novaflow-consumer-group
    consume-thread-min: 5
    consume-thread-max: 10
    consume-timeout: 15
    max-reconsume-times: 3  # 最大重试次数
```

## Topic 和 Tag 架构

### Topics
| Topic | 用途 |
|-------|------|
| `video-processing-topic` | 视频处理相关消息 |
| `recommendation-topic` | 推荐生成相关消息 |

### Tags
| Tag | 用途 |
|-----|------|
| `video-upload` | 视频上传消息 |
| `video-analysis` | 视频分析消息 |
| `recommendation` | 推荐消息 |

### Consumer Groups
| Consumer Group | 用途 |
|----------------|------|
| `video-upload-consumer-group` | 视频上传消费者 |
| `video-analysis-consumer-group` | 视频分析消费者 |
| `recommendation-consumer-group` | 推荐消费者 |

## 使用示例

### 发送消息

```java
@Autowired
private VideoProcessingProducer videoProcessingProducer;

// 发送视频上传消息
videoProcessingProducer.sendVideoUploadMessage(videoId, userId);

// 发送视频分析消息
videoProcessingProducer.sendVideoAnalysisMessage(videoId, userId);
```

```java
@Autowired
private RecommendationProducer recommendationProducer;

// 发送推荐消息
recommendationProducer.sendRecommendationMessage(recommendationId, userId, videoId);
```

### 消费消息

消费者已自动注册，通过 `@RocketMQMessageListener` 注解监听指定 topic 和 tag：

- `VideoUploadConsumer`: 处理视频上传消息
- `VideoAnalysisConsumer`: 处理视频分析消息
- `RecommendationConsumer`: 处理推荐消息

## 消息重试机制

1. **自动重试**: 消费失败后自动重试3次（可配置）
2. **重试间隔**: 第1次：10秒，第2次：30秒，第3次：60秒
3. **超过重试次数**: 消息将被丢弃，记录日志

## 死信消息处理

与 RabbitMQ 不同，RocketMQ 没有内置的死信队列机制。处理失败消息的方式：

1. **日志记录**: 失败消息会记录错误日志
2. **持久化存储**: 可以将失败消息保存到数据库
3. **告警通知**: 发送告警通知给运维人员

## 监控建议

1. 监控消息堆积量
2. 监控消费速率
3. 监控消费失败率
4. 设置告警阈值

## 本地开发

### 启动 RocketMQ NameServer (使用 Docker)

```bash
# 启动 NameServer
docker run -d --name rocketmq-namesrv \
  -p 9876:9876 \
  apache/rocketmq:5.3.1 \
  sh mqnamesrv

# 启动 Broker
docker run -d --name rocketmq-broker \
  -p 10909:10909 -p 10911:10911 -p 10912:10912 \
  --link rocketmq-namesrv:namesrv \
  -e "NAMESRV_ADDR=namesrv:9876" \
  apache/rocketmq:5.3.1 \
  sh mqbroker -n namesrv:9876 -c /opt/rocketmq/conf/broker.conf
```

### 使用 Docker Compose (推荐)

创建 `docker-compose.yml`:

```yaml
version: '3'
services:
  rocketmq-namesrv:
    image: apache/rocketmq:5.3.1
    container_name: rocketmq-namesrv
    ports:
      - "9876:9876"
    command: sh mqnamesrv

  rocketmq-broker:
    image: apache/rocketmq:5.3.1
    container_name: rocketmq-broker
    ports:
      - "10909:10909"
      - "10911:10911"
      - "10912:10912"
    depends_on:
      - rocketmq-namesrv
    environment:
      - NAMESRV_ADDR=rocketmq-namesrv:9876
    command: sh mqbroker -n rocketmq-namesrv:9876
```

启动：`docker-compose up -d`

## RocketMQ Console

RocketMQ 提供了 Web 控制台，可以查看和管理消息：

```bash
# 克隆项目
git clone https://github.com/apache/rocketmq-dashboard.git

# 启动控制台
cd rocketmq-dashboard
mvn spring-boot:run
```

访问：http://localhost:8080

## 与 RabbitMQ 的主要区别

| 特性 | RabbitMQ | RocketMQ |
|------|----------|----------|
| 消息模型 | Queue | Topic/Tag |
| 死信队列 | 内置支持 | 需要自行实现 |
| 消息顺序 | 支持 | 支持 |
| 消息堆积 | 较弱 | 极强 |
| 吞吐量 | 万级 | 百万级 |
| 时序消息 | 不支持 | 支持 |
| 事务消息 | 不支持 | 支持 |

## 故障排查

### 问题1: 连接失败
- 检查 NameServer 是否运行
- 检查连接配置是否正确
- 检查防火墙设置

### 问题2: 消息发送失败
- 检查 Topic 是否已创建
- 检查 Broker 是否运行正常
- 查看错误日志

### 问题3: 消息堆积
- 增加消费者数量
- 优化消费逻辑
- 检查是否有消费失败导致重试

### 问题4: 消费失败
- 查看消费者日志
- 检查业务逻辑是否正确
- 检查数据格式是否匹配
