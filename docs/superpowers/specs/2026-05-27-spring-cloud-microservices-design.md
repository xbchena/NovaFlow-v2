# NovaFlow Spring Cloud 微服务化设计

**日期**: 2026-05-27
**项目**: NovaFlow (不想思考了 - 智能饮食推荐系统)
**范围**: 从单体 DDD 架构迁移到 Spring Cloud 微服务架构

## 背景与动机

NovaFlow 当前采用 Spring Boot 单体 + DDD 分层架构，已完成约 85% 的代码实现（141 个 Java 文件），包括完整的领域模型、应用服务、持久化层和控制器。随着业务增长（预计日活 1k-10k），需要将单体应用拆分为微服务，以获得独立部署、独立扩展和更好的服务治理能力。

### 设计目标

- 按 DDD 限界上下文拆分微服务，保持领域逻辑清晰
- 部署在阿里云平台，利用阿里云生态
- 引入 Spring Cloud Alibaba 全家桶实现服务治理
- 最大程度复用现有代码，降低迁移风险

## 技术选型

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 核心框架 | Spring Boot | 3.2.x | 基础框架 |
| 微服务框架 | Spring Cloud | 2023.0.x | 微服务标准 |
| 微服务增强 | Spring Cloud Alibaba | 2023.0.x | 阿里云生态集成 |
| 注册/配置中心 | Nacos | 2.3.x | 服务发现 + 配置管理 |
| API 网关 | Spring Cloud Gateway | 4.1.x | 统一入口、路由、鉴权 |
| 服务调用 | OpenFeign | 4.1.x | 声明式 HTTP 客户端 |
| 熔断限流 | Sentinel | 1.8.x | 流量控制、熔断降级 |
| 分布式事务 | Seata | 2.0.x | AT 模式（按需引入） |
| 链路追踪 | SkyWalking | 9.x | 分布式追踪 |
| 消息队列 | RabbitMQ | 3.12+ | 异步事件驱动 |
| 数据库 | MySQL | 8.0 | 每服务独立 schema |
| 缓存 | Redis | 7.0 | 共享实例，前缀隔离 |
| 对象存储 | 阿里云 OSS | 3.17.4 | 视频文件存储 |
| AI 模型 | 通义千问 DashScope | Latest | 视频分析 + 智能推荐 |
| 容器化 | Docker + K8s (ACK) | Latest | 生产部署 |

## 整体架构

```
                    ┌─────────────────┐
                    │   Mobile App    │
                    │  (React Native) │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  Spring Cloud   │  端口: 8080
                    │    Gateway      │  JWT鉴权 + 路由 + 限流
                    └────────┬────────┘
                             │
          ┌──────────┬───────┼───────┬──────────┐
          │          │       │       │          │
   ┌──────▼──────┐ ┌─▼────┐ ┌▼─────┐ ┌▼────────┐
   │ Auth Service│ │Video │ │Recmd │ │Feedback │
   │   :8081     │ │:8082 │ │:8083 │ │  :8084  │
   └──────┬──────┘ └──┬───┘ └──┬───┘ └────┬────┘
          │           │        │           │
          ▼           ▼        ▼           ▼
       ┌─────┐    ┌─────┐  ┌─────┐    ┌─────┐
       │MySQL│    │MySQL│  │MySQL│    │MySQL│
       │ auth│    │video│  │rcmd │    │feed │
       └─────┘    └─────┘  └─────┘    └─────┘

   ┌─────────────────────────────────────────┐
   │          共享基础设施                     │
   │  Nacos (注册/配置)  Redis (缓存)         │
   │  RabbitMQ (消息)    OSS (文件)           │
   │  SkyWalking (追踪)  通义千问 (AI)        │
   └─────────────────────────────────────────┘
```

## Maven 多模块结构

```
NovaFlow-v2/
├── pom.xml                              # 父 POM (dependency management)
├── novaflow-common/                     # 公共模块
│   ├── Result.java                      # 统一响应
│   ├── ErrorCode.java                   # 错误码枚举
│   ├── GlobalExceptionHandler.java      # 全局异常处理
│   ├── dto/                             # 跨服务共享 DTO
│   └── util/                            # 工具类
├── novaflow-gateway/                    # API 网关服务
├── novaflow-auth-service/               # 认证服务
├── novaflow-video-service/              # 视频处理服务
├── novaflow-recommendation-service/     # 智能推荐服务
├── novaflow-feedback-service/           # 用户反馈服务
└── mobile-app/                          # 移动端 (保持不变)
```

## 服务拆分详情

### Auth Service (端口 8081)

**职责**: 用户认证、授权、用户信息管理

**核心能力**:
- 微信 OAuth 2.0 登录（WxJava SDK）
- 手机号验证码登录（阿里云 SMS）
- JWT Access Token + Refresh Token 签发与验证
- 用户偏好设置管理

**内部结构**（复用现有 DDD 分层）:
```
novaflow-auth-service/
├── interfaces/rest/AuthController.java
├── application/service/AuthApplicationService.java
├── domain/model/auth/              # User 聚合根 + 值对象
├── infrastructure/
│   ├── persistence/                # UserRepositoryImpl + UserMapper
│   ├── external/                   # WeChatAuthServiceImpl, SmsServiceImpl
│   └── security/                   # TokenServiceImpl
└── resources/bootstrap.yml         # Nacos 配置
```

**数据库**: `novaflow_auth` schema — user 表

### Video Service (端口 8082)

**职责**: 视频上传、存储、处理状态管理

**核心能力**:
- 视频上传（阿里云 OSS 直传 + 断点续传）
- 视频状态机管理（INITIALIZED → UPLOADING → PROCESSING → COMPLETED/FAILED）
- 视频元数据管理（位置、时长、文件大小）
- 视频处理完成后发布 RabbitMQ 消息触发推荐

**内部结构**:
```
novaflow-video-service/
├── interfaces/rest/VideoController.java
├── application/service/VideoProcessingApplicationService.java
├── domain/model/video/             # Video 聚合根 + 状态机
├── infrastructure/
│   ├── persistence/                # VideoRepositoryImpl + VideoMapper
│   ├── external/                   # OSSStorageServiceImpl
│   └── messaging/                  # RabbitMQ Producer
└── resources/bootstrap.yml
```

**数据库**: `novaflow_video` schema — video 表

**异步事件**: 视频处理完成 → 发布 `VideoProcessingCompletedEvent` 到 RabbitMQ

### Recommendation Service (端口 8083)

**职责**: AI 视频分析、智能推荐生成、推荐去重

**核心能力**:
- 消费视频处理完成消息（RabbitMQ Consumer）
- 调用通义千问进行视频内容分析
- 基于场景（菜市场/餐馆/食堂）、餐段、价格、人数、心情生成推荐
- 通过 Feign 查询 Feedback 服务获取历史记录进行去重
- 推荐结果缓存（Redis）

**内部结构**:
```
novaflow-recommendation-service/
├── interfaces/rest/RecommendationController.java
├── application/service/RecommendationApplicationService.java
├── domain/model/recommendation/    # Recommendation 聚合根 + 去重服务
├── infrastructure/
│   ├── persistence/                # RecommendationRepositoryImpl
│   ├── external/                   # 通义千问 AI 客户端, MapService
│   ├── feign/                      # VideoFeignClient, FeedbackFeignClient
│   └── messaging/                  # RabbitMQ Consumer
└── resources/bootstrap.yml
```

**数据库**: `novaflow_recommendation` schema — recommendation, recommendation_history 表

### Feedback Service (端口 8084)

**职责**: 用户选择记录、反馈收集与评分

**核心能力**:
- 记录用户对推荐项的选择（accept/reject/skip）
- 收集用户反馈评分和文本
- 发布反馈事件供推荐服务优化权重
- 提供用户选择记录查询接口（供 Recommendation 服务 Feign 调用）

**内部结构**:
```
novaflow-feedback-service/
├── interfaces/rest/FeedbackController.java
├── application/service/UserFeedbackApplicationService.java
├── domain/model/feedback/          # UserSelection 聚合根
├── infrastructure/
│   ├── persistence/                # UserSelectionRepositoryImpl
│   └── messaging/                  # RabbitMQ Producer
└── resources/bootstrap.yml
```

**数据库**: `novaflow_feedback` schema — user_selection 表

## 服务间通信

### 同步调用 (OpenFeign)

| 调用方 | 被调方 | 接口 | 用途 |
|--------|--------|------|------|
| Recommendation | Video | `GET /internal/video/{id}` | 获取视频详情用于 AI 分析 |
| Recommendation | Feedback | `GET /internal/feedback/selections` | 查询用户历史选择记录用于去重和优化 |

所有 Feign 调用配置 Sentinel 降级和重试策略。

### 异步消息 (RabbitMQ)

| 生产者 | 消费者 | Exchange/Queue | 事件 |
|--------|--------|---------------|------|
| Video Service | Recommendation Service | `video.exchange` / `recommendation.queue` | `VideoProcessingCompletedEvent` |
| Feedback Service | Recommendation Service | `feedback.exchange` / `recommendation.optimize.queue` | `FeedbackSubmittedEvent` |

消息格式统一使用 JSON，包含事件类型、聚合 ID、时间戳和业务数据。

## API 网关设计

### 路由规则

| 路径模式 | 目标服务 | 鉴权 |
|----------|----------|------|
| `/api/auth/**` | novaflow-auth-service | 部分接口免鉴权 |
| `/api/user/**` | novaflow-auth-service | 需要 JWT |
| `/api/video/**` | novaflow-video-service | 需要 JWT |
| `/api/recommendation/**` | novaflow-recommendation-service | 需要 JWT |
| `/api/feedback/**` | novaflow-feedback-service | 需要 JWT |
| `/internal/**` | 禁止外部访问 | 仅服务间调用 |

### 全局过滤器

1. **JWT 鉴权过滤器**: 解析 Authorization Header → 使用共享 JWT Secret 本地验证（无需 Feign 调用 Auth 服务）→ 检查 Redis 黑名单（logout/token 撤销）→ 将 userId、openid 注入请求头传递给下游
2. **请求日志过滤器**: 记录请求路径、方法、耗时、状态码，生成唯一 traceId
3. **跨域配置**: 统一处理移动端跨域请求

### 白名单路径（免鉴权）

- `POST /api/auth/wechat/login`
- `POST /api/auth/phone/send-code`
- `POST /api/auth/phone/login`
- `POST /api/auth/refresh-token`

### 限流策略 (Sentinel)

| 规则 | 限制 |
|------|------|
| 全局每用户 | 100 次/分钟 |
| 推荐接口 | 10 次/分钟（AI 调用成本高） |
| 视频上传 | 5 次/分钟 |
| 登录接口 | 10 次/分钟（防暴力破解） |

## Nacos 配置管理

### 配置分组

```yaml
# 公共配置 (所有服务共享)
novaflow-common.yaml:
  spring.redis.*          # Redis 连接
  logging.level.*         # 日志级别

# 各服务独立配置
novaflow-gateway.yaml:            # 路由规则、限流参数
novaflow-auth-service.yaml:       # JWT密钥、微信AppID/Secret、短信配置
novaflow-video-service.yaml:      # OSS AccessKey、RabbitMQ连接
novaflow-recommendation-service.yaml:  # AI API Key、推荐参数、Feign超时
novaflow-feedback-service.yaml:   # 反馈相关参数
```

### 敏感配置处理

- 数据库密码、JWT Secret、API Key 等通过 Nacos 加密配置存储
- 或使用环境变量注入（`${DB_PASSWORD}`）
- 生产环境禁止在代码或配置文件中硬编码敏感信息

## 容错与可观测性

### 熔断降级 (Sentinel)

- 所有 Feign 调用配置降级方法，返回默认值或友好错误提示
- AI 服务调用超时阈值：30 秒，超时后返回缓存的推荐结果
- OSS 上传失败时自动重试 3 次，仍失败则标记视频状态为 FAILED

### 链路追踪 (SkyWalking)

- 每个请求自动注入 traceId，跨服务传递
- 记录完整调用链：Gateway → Service → Database/External
- 异常自动上报，支持按 traceId 查询完整日志

### 日志聚合

- 统一使用 Logback + JSON 格式输出
- 阿里云日志服务 (SLS) 或 ELK 收集
- 日志包含 traceId、userId、service 名，便于关联查询

### 健康检查

- Spring Boot Actuator 暴露 `/actuator/health`
- K8s liveness/readiness probe 集成
- Nacos 心跳检测，自动摘除不健康实例

## 数据库隔离策略

每个服务拥有独立 MySQL schema：

| 服务 | Schema | 表 |
|------|--------|-----|
| Auth Service | `novaflow_auth` | user |
| Video Service | `novaflow_video` | video |
| Recommendation Service | `novaflow_recommendation` | recommendation, recommendation_history |
| Feedback Service | `novaflow_feedback` | user_selection |

**Redis 前缀隔离**: 各服务使用不同前缀（如 `auth:`, `video:`, `rcmd:`, `fb:`）共享同一 Redis 实例。

## 部署架构

### 开发/测试环境 (Docker Compose)

```yaml
services:
  nacos:        # 注册中心 + 配置中心
  gateway:      # API 网关
  auth-service: # 认证服务
  video-service:
  recommendation-service:
  feedback-service:
  mysql:        # 多 schema
  redis:
  rabbitmq:
```

### 生产环境 (阿里云 ACK)

```
负载均衡 (SLB) → Spring Cloud Gateway (多副本)
                       ↓
              ACK K8s Cluster
              ├── auth-service Deployment (2 副本)
              ├── video-service Deployment (2 副本)
              ├── recommendation-service Deployment (3 副本)
              └── feedback-service Deployment (2 副本)
                       ↓
              阿里云托管服务
              ├── RDS MySQL (主从)
              ├── 云 Redis (哨兵)
              ├── 云 RabbitMQ
              ├── OSS
              └── Nacos (MSE 托管 或 自建)
```

## 迁移策略

### 阶段 1：项目结构重组 (1 周)

- 创建 Maven 多模块父 POM，统一管理依赖版本
- 抽取 `novaflow-common` 公共模块（Result, ErrorCode, 异常, 工具类）
- 按服务拆分源码到各自模块，保持 DDD 分层结构
- 每个模块添加 `bootstrap.yml` 配置 Nacos 连接
- 验证各服务能独立启动并注册到 Nacos

### 阶段 2：网关与注册中心 (3 天)

- 搭建 Nacos 服务（开发环境 Docker）
- 实现 Spring Cloud Gateway 模块
- 配置路由规则、JWT 过滤器、跨域配置
- 验证端到端请求链路（Client → Gateway → Service → DB）

### 阶段 3：服务间通信 (1 周)

- 定义 Feign Client 接口（Video, Feedback 的内部接口）
- 配置 RabbitMQ Exchange/Queue/Binding
- 实现 Video → Recommendation 异步流程
- 实现 Recommendation ↔ Feedback 同步调用
- 配置 Sentinel 熔断降级规则

### 阶段 4：数据层隔离 (3 天)

- 每个服务创建独立 MySQL schema
- 编写数据迁移脚本，将现有数据分配到对应 schema
- 移除所有跨服务直接数据库访问
- 验证数据完整性和查询正确性

### 阶段 5：容器化与部署 (1 周)

- 编写各服务 Dockerfile（多阶段构建，减小镜像体积）
- 编写 docker-compose.yml（开发/测试环境）
- 配置阿里云效 CI/CD 流水线
- 部署到 ACK 测试环境，执行集成测试

### 风险与应对

| 风险 | 应对 |
|------|------|
| 分布式事务复杂性 | 优先最终一致性（MQ + 本地事务表），仅在关键路径引入 Seata AT |
| 服务间调用延迟 | Redis 缓存热点数据，减少同步调用链长度，合理设置超时 |
| 配置管理混乱 | Nacos 统一管理，环境变量覆盖，敏感配置加密 |
| 调试困难 | SkyWalking 链路追踪 + 统一 traceId + 日志聚合 |
| 迁移期间不可用 | 保留单体应用作为降级方案，通过网关逐步切换流量 |

## 决策记录

| 决策 | 选择 | 理由 |
|------|------|------|
| 注册/配置中心 | Nacos | 阿里云生态深度集成，注册+配置一体，国内社区成熟 |
| API 网关 | Spring Cloud Gateway | 与 Spring Cloud 生态无缝集成，支持响应式 |
| 熔断限流 | Sentinel | Spring Cloud Alibaba 标配，功能全面 |
| 消息队列 | RabbitMQ | 项目已使用，轻量级适合中等规模 |
| 数据库策略 | 每服务独立 schema | 数据隔离，避免耦合，便于独立备份和扩展 |
| 拆分粒度 | 按 DDD 限界上下文 | 现有领域边界清晰，4 个服务适中 |
| 部署平台 | 阿里云 ACK | 与现有阿里云资源（OSS、RDS、DashScope）协同 |
