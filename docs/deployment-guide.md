# NovaFlow-v2 初始化与部署手册

> **版本**: 2.0.0-SNAPSHOT  
> **更新日期**: 2026-05-31  
> **适用环境**: 开发 / 测试 / 生产

---

## 目录

1. [系统架构概览](#1-系统架构概览)
2. [环境要求](#2-环境要求)
3. [基础设施安装](#3-基础设施安装)
4. [数据库初始化](#4-数据库初始化)
5. [项目构建](#5-项目构建)
6. [配置指南](#6-配置指南)
7. [服务启动顺序](#7-服务启动顺序)
8. [API 网关路由](#8-api-网关路由)
9. [健康检查](#9-健康检查)
10. [常见问题排查](#10-常见问题排查)

---

## 1. 系统架构概览

```
                        ┌──────────────┐
                        │   小程序 / H5  │
                        └──────┬───────┘
                               │
                    ┌──────────▼──────────┐
                    │  novaflow-gateway     │  :8080
                    │  Spring Cloud Gateway│
                    └──┬────┬────┬────┬────┘
                       │    │    │    │
          ┌────────────▼┐ ┌─▼────▼──┐ ┌▼──────────────┐ ┌───────────────┐
          │ auth-service │ │ video   │ │ recommendatio  │ │ feedback      │
          │   :8081      │ │ :8082   │ │    :8083       │ │   :8084       │
          └─────────────┘ └─────────┘ └───────────────┘ └───────────────┘
```

### 模块说明

| 模块 | 端口 | 职责 | 关键依赖 |
|------|------|------|----------|
| `novaflow-common` | — | 共享 DDD 内核、值对象、工具类 | 无外部依赖 |
| `novaflow-gateway` | 8080 | API 网关、路由、JWT 鉴权 | Nacos, Redis, Sentinel |
| `novaflow-auth-service` | 8081 | 微信登录、用户管理、Token 签发 | MySQL, Redis, WxJava |
| `novaflow-video-service` | 8082 | 视频上传、存储到 OSS、帧提取 | MySQL, Redis, RocketMQ, Aliyun OSS |
| `novaflow-recommendation-service` | 8083 | AI 视频分析、智能推荐、Agent 对话 | MySQL, Redis, RocketMQ, Spring AI DashScope |
| `novaflow-feedback-service` | 8084 | 用户选择记录、反馈收集 | MySQL, RocketMQ |

### 技术栈版本

| 技术 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.2.5 |
| Spring Cloud | 2023.0.1 |
| Spring Cloud Alibaba | 2023.0.1.0 |
| Spring AI Alibaba (DashScope) | 1.1.2.2 |
| MyBatis Plus | 3.5.7 |
| RocketMQ Spring Boot | 2.2.3 |
| WxJava | 4.8.1.B |
| JWT (jjwt) | 0.12.6 |
| Aliyun OSS SDK | 3.18.1 |

---

## 2. 环境要求

### 开发环境

| 组件 | 最低版本 | 说明 |
|------|----------|------|
| JDK | 21+ | 推荐 Eclipse Temurin 21 |
| Maven | 3.9+ | 推荐 3.9.6+ |
| MySQL | 8.0+ | 推荐 8.0.35+ |
| Redis | 6.0+ | 推荐 7.0+ |
| RocketMQ | 5.0+ | 推荐 5.1.0+ |
| Nacos | 2.3+ | 推荐 2.3.0+ |

### 外部服务（需要 API Key）

| 服务 | 用途 | 获取地址 |
|------|------|----------|
| 阿里云 DashScope | AI 大模型（通义千问） | https://dashscope.console.aliyun.com |
| 阿里云 OSS | 视频文件存储 | https://oss.console.aliyun.com |
| 高德地图 | 餐厅位置搜索 | https://lbs.amap.com |
| 微信开放平台 | 小程序登录 | https://mp.weixin.qq.com |

### 生产环境额外要求

| 组件 | 说明 |
|------|------|
| Nginx | 反向代理、HTTPS 终结、负载均衡 |
| Docker / K8s | 容器化部署（可选） |
| Prometheus + Grafana | 监控（可选，配合 Spring Boot Actuator） |

---

## 3. 基础设施安装

### 3.1 JDK 21

```bash
# macOS (Homebrew)
brew install openjdk@21

# 验证
java -version
# 预期输出: openjdk version "21.x.x"

# 设置 JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

### 3.2 Maven

```bash
# macOS (Homebrew)
brew install maven

# 验证
mvn -version
```

### 3.3 MySQL 8.0

```bash
# macOS (Homebrew)
brew install mysql
brew start mysql

# 创建数据库和用户
mysql -u root -p

-- 执行以下 SQL:
CREATE DATABASE IF NOT EXISTS novaflow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'novaflow'@'%' IDENTIFIED BY 'novaflow123';
GRANT ALL PRIVILEGES ON novaflow.* TO 'novaflow'@'%';
FLUSH PRIVILEGES;
```

### 3.4 Redis

```bash
# macOS (Homebrew)
brew install redis
brew start redis

# 验证
redis-cli ping
# 预期输出: PONG
```

### 3.5 RocketMQ

```bash
# macOS (Homebrew)
brew install rocketmq

# 启动 Name Server
mqnamesrv &

# 启动 Broker
mqbroker -n localhost:9876 &

# 验证（检查进程）
ps aux | grep mqnamesrv
ps aux | grep mqbroker
```

> **Docker 方式（推荐开发用）**:
> ```bash
> docker run -d --name rmqnamesrv -p 9876:9876 apache/rocketmq:5.1.0 sh mqnamesrv
> docker run -d --name rmqbroker -p 10911:10911 -p 10909:10909 \
>   -e "NAMESRV_ADDR=host.docker.internal:9876" \
>   apache/rocketmq:5.1.0 sh mqbroker -n host.docker.internal:9876
> ```

### 3.6 Nacos

```bash
# 下载
wget https://github.com/alibaba/nacos/releases/download/2.3.0/nacos-server-2.3.0.tar.gz
tar -xzf nacos-server-2.3.0.tar.gz
cd nacos/bin

# 单机启动（开发模式）
bash startup.sh -m standalone

# 验证
curl http://localhost:8848/nacos/v1/ns/operator/leaders
# 预期输出: JSON 格式的 leader 信息

# 控制台: http://localhost:8848/nacos (默认账号密码: nacos/nacos)
```

> **Docker 方式**:
> ```bash
> docker run -d --name nacos \
>   -e MODE=standalone \
>   -p 8848:8848 \
>   -p 9848:9848 \
>   nacos/nacos-server:v2.3.0
> ```

---

## 4. 数据库初始化

### 4.1 执行建表 SQL

按顺序执行以下 SQL 文件（目前只有 `user_profile`，其余表需通过 MyBatis Plus 自动建表或手动创建）：

```bash
mysql -u novaflow -pnovaflow123 novaflow < \
  novaflow-recommendation-service/src/main/resources/db/schema/user_profile.sql
```

### 4.2 完整建表 DDL

以下为所有服务的数据库表定义，请在 `novaflow` 数据库中执行：

```sql
-- ============================================================
-- 认证服务表 (auth-service)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(128) DEFAULT NULL COMMENT '微信 openid',
    unionid VARCHAR(128) DEFAULT NULL COMMENT '微信 unionid',
    nickname VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    avatar VARCHAR(512) DEFAULT NULL COMMENT '头像 URL',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    preferences JSON DEFAULT NULL COMMENT '偏好设置',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 视频服务表 (video-service)
-- ============================================================
CREATE TABLE IF NOT EXISTS videos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '上传用户ID',
    status VARCHAR(32) NOT NULL DEFAULT 'UPLOADED' COMMENT '状态: UPLOADED/PROCESSING/COMPLETED/FAILED',
    storage_info JSON DEFAULT NULL COMMENT 'OSS 存储信息',
    metadata JSON DEFAULT NULL COMMENT '视频元数据',
    location JSON DEFAULT NULL COMMENT '位置信息',
    error_message VARCHAR(512) DEFAULT NULL COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频表';

-- ============================================================
-- 推荐服务表 (recommendation-service)
-- ============================================================
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    video_id BIGINT DEFAULT NULL COMMENT '视频ID',
    content JSON DEFAULT NULL COMMENT '推荐内容 (JSON)',
    scene_type VARCHAR(64) DEFAULT NULL COMMENT '场景类型',
    scene_description TEXT DEFAULT NULL COMMENT '场景描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_user_id (user_id),
    KEY idx_video_id (video_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐记录表';

CREATE TABLE IF NOT EXISTS recommendation_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    history_data JSON DEFAULT NULL COMMENT '推荐历史数据',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐历史表';

CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    diet_preferences JSON COMMENT '饮食偏好，如 ["辣味","火锅"]',
    allergies JSON COMMENT '过敏信息，如 ["花生","海鲜"]',
    preferred_cuisines JSON COMMENT '喜好菜系，如 ["川菜","湘菜"]',
    preferred_price_range VARCHAR(20) COMMENT '价格偏好：低/中/高',
    behavior_summary JSON COMMENT '行为摘要',
    total_recommendations INT DEFAULT 0 COMMENT '累计推荐次数',
    total_clicks INT DEFAULT 0 COMMENT '累计点击次数',
    updated_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表 - L3长期记忆';

-- ============================================================
-- 反馈服务表 (feedback-service)
-- ============================================================
CREATE TABLE IF NOT EXISTS user_selections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    video_id BIGINT DEFAULT NULL COMMENT '视频ID',
    recommendation_id BIGINT DEFAULT NULL COMMENT '推荐ID',
    food_name VARCHAR(128) DEFAULT NULL COMMENT '选择的食物',
    accepted TINYINT(1) DEFAULT NULL COMMENT '是否接受推荐',
    feedback VARCHAR(512) DEFAULT NULL COMMENT '用户反馈',
    selected_at DATETIME DEFAULT NULL COMMENT '选择时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户选择记录表';
```

---

## 5. 项目构建

### 5.1 克隆项目

```bash
git clone <repository-url> NovaFlow-v2
cd NovaFlow-v2
```

### 5.2 全量构建

```bash
# 编译全部模块（跳过测试）
mvn clean compile -DskipTests

# 完整构建（含测试）
mvn clean package -DskipTests

# 构建成功后，各服务的 JAR 包位置：
# novaflow-gateway/target/novaflow-gateway-2.0.0-SNAPSHOT.jar
# novaflow-auth-service/target/novaflow-auth-service-2.0.0-SNAPSHOT.jar
# novaflow-video-service/target/novaflow-video-service-2.0.0-SNAPSHOT.jar
# novaflow-recommendation-service/target/novaflow-recommendation-service-2.0.0-SNAPSHOT.jar
# novaflow-feedback-service/target/novaflow-feedback-service-2.0.0-SNAPSHOT.jar
```

### 5.3 单独构建某个模块

```bash
# 只构建 recommendation-service 及其依赖
mvn clean package -pl novaflow-recommendation-service -am -DskipTests
```

---

## 6. 配置指南

### 6.1 配置优先级

```
Nacos 配置中心 (最高) > bootstrap.yml 环境变量 > application.yml > 默认值
```

### 6.2 必须修改的配置项

以下配置项在 `application.yml` 中使用占位符，**部署前必须替换**：

#### 所有服务 — 数据库连接

文件：`novaflow-{service}/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://<MYSQL_HOST>:3306/novaflow?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      username: <MYSQL_USER>
      password: <MYSQL_PASSWORD>
```

#### 所有服务 — Redis 连接

```yaml
spring:
  data:
    redis:
      host: <REDIS_HOST>
      port: <REDIS_PORT>
      password: <REDIS_PASSWORD>  # 可选
```

#### 推荐服务 — DashScope API Key

文件：`novaflow-recommendation-service/src/main/resources/application.yml`

```yaml
app:
  ai:
    tongyi:
      api-key: <你的_DASHSCOPE_API_KEY>  # 必须替换！
```

> **获取方式**：登录 [DashScope 控制台](https://dashscope.console.aliyun.com) → API-KEY 管理 → 创建 API Key

#### 视频服务 — 阿里云 OSS

文件：`novaflow-video-service/src/main/resources/application.yml`

```yaml
aliyun:
  oss:
    endpoint: <OSS_ENDPOINT>          # 如: oss-cn-hangzhou.aliyuncs.com
    access-key-id: <你的_ACCESS_KEY_ID>
    access-key-secret: <你的_ACCESS_KEY_SECRET>
    bucket-name: <BUCKET_NAME>        # 如: novaflow-videos
```

#### 推荐服务 — 高德地图 API

文件：`novaflow-recommendation-service/src/main/resources/application.yml`

```yaml
app:
  amap:
    api-key: <你的_高德_API_KEY>
    secret-key: <你的_高德_SECRET_KEY>
```

#### 认证服务 — 微信小程序

文件：`novaflow-auth-service/src/main/resources/application.yml`

```yaml
app:
  wechat:
    app-id: <你的_微信_APPID>
    app-secret: <你的_微信_APPSECRET>
```

#### 网关 + 认证服务 — JWT Secret

文件：`novaflow-gateway/src/main/resources/application.yml` 和 `novaflow-auth-service/src/main/resources/application.yml`

```yaml
app:
  jwt:
    secret: <生产环境使用强随机字符串>  # 必须替换！建议 32+ 字符
```

> **生成方式**: `openssl rand -base64 32`

### 6.3 可选配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `app.ai.tongyi.model` | qwen-max | AI 模型选择（qwen-max/qwen-plus/qwen-turbo） |
| `app.ai.tongyi.temperature` | 0.7 | 模型随机性 (0-1) |
| `app.ai.tongyi.max-tokens` | 2000 | 最大输出 token 数 |
| `app.ai.agent.max-iterations` | 10 | Agent 最大推理循环次数 |
| `app.ai.chat.memory.max-messages` | 50 | 短期记忆保留消息数 |
| `app.ai.chat.memory.ttl-hours` | 24 | 聊天记录保留时间（小时） |

### 6.4 环境变量方式（推荐生产使用）

```bash
# 设置环境变量后，bootstrap.yml 中的 ${NACOS_SERVER_ADDR} 等占位符会自动解析

export NACOS_SERVER_ADDR=nacos.example.com:8848
export NACOS_NAMESPACE=production
export SPRING_PROFILES_ACTIVE=prod

# 启动服务时会自动读取
java -jar novaflow-recommendation-service.jar
```

### 6.5 Nacos 配置中心（推荐）

在 Nacos 控制台创建共享配置 `novaflow-common.yaml`（Data ID: `novaflow-common.yaml`, Group: `DEFAULT_GROUP`）：

```yaml
# 所有服务共享的公共配置
spring:
  datasource:
    druid:
      url: jdbc:mysql://mysql-prod.example.com:3306/novaflow?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
      username: ${DB_USERNAME}
      password: ${DB_PASSWORD}
  data:
    redis:
      host: ${REDIS_HOST}
      port: 6379
      password: ${REDIS_PASSWORD}
```

各服务在 `bootstrap.yml` 中已配置 `shared-configs` 会自动拉取此共享配置。

---

## 7. 服务启动顺序

### 7.1 启动依赖关系

```
Nacos → Redis → MySQL → RocketMQ → [各业务服务] → Gateway
```

### 7.2 开发环境启动（IDE 方式）

在 IntelliJ IDEA 或 Eclipse 中：

1. **启动基础设施**：确保 MySQL、Redis、RocketMQ、Nacos 均已启动
2. **启动 auth-service** (主类: `AuthServiceApplication`, :8081)
3. **启动 video-service** (主类: `VideoServiceApplication`, :8082)
4. **启动 recommendation-service** (主类: `RecommendationServiceApplication`, :8083)
5. **启动 feedback-service** (主类: `FeedbackServiceApplication`, :8084)
6. **启动 gateway** (主类: `GatewayApplication`, :8080)

> 每个服务启动后，会在 Nacos 控制台的"服务列表"中注册。

### 7.3 命令行启动

```bash
# 1. 全量构建
mvn clean package -DskipTests

# 2. 按顺序启动各服务（每个使用独立终端）

# Auth Service
java -jar novaflow-auth-service/target/novaflow-auth-service-2.0.0-SNAPSHOT.jar &

# Video Service
java -jar novaflow-video-service/target/novaflow-video-service-2.0.0-SNAPSHOT.jar &

# Recommendation Service
java -jar novaflow-recommendation-service/target/novaflow-recommendation-service-2.0.0-SNAPSHOT.jar &

# Feedback Service
java -jar novaflow-feedback-service/target/novaflow-feedback-service-2.0.0-SNAPSHOT.jar &

# Gateway (最后启动)
java -jar novaflow-gateway/target/novaflow-gateway-2.0.0-SNAPSHOT.jar &
```

### 7.4 指定 JVM 参数（生产推荐）

```bash
java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -Dserver.port=8083 \
     -DNACOS_SERVER_ADDR=nacos:8848 \
     -DSPRING_PROFILES_ACTIVE=prod \
     -jar novaflow-recommendation-service-2.0.0-SNAPSHOT.jar
```

---

## 8. API 网关路由

所有外部请求通过 Gateway (:8080) 路由到各服务：

| 路径模式 | 目标服务 | 说明 |
|----------|----------|------|
| `/api/auth/**` | auth-service :8081 | 登录、注册、Token 刷新 |
| `/api/user/**` | auth-service :8081 | 用户信息管理 |
| `/api/video/**` | video-service :8082 | 视频上传、列表、状态查询 |
| `/api/recommendation/**` | recommendation-service :8083 | 推荐生成、历史查询 |
| `/api/feedback/**` | feedback-service :8084 | 用户选择记录、反馈提交 |

### 认证服务独立端点（不经网关）

以下端点可直接访问各服务（用于内部调用或调试）：

| 服务 | 端点 | 说明 |
|------|------|------|
| recommendation-service | `POST /api/v1/chat/message` | AI Agent 对话（核心） |
| recommendation-service | `POST /api/v1/chat/analyze-frame` | 视频帧分析 |
| recommendation-service | `DELETE /api/v1/chat/session/{sessionId}` | 清除会话记忆 |
| recommendation-service | `GET /api/v1/chat/scene/{sessionId}` | 获取场景上下文 |
| recommendation-service | `GET /api/v1/profile/{userId}` | 获取用户画像 |
| recommendation-service | `PUT /api/v1/profile/{userId}/preferences` | 更新饮食偏好 |
| recommendation-service | `PUT /api/v1/profile/{userId}/allergies` | 更新过敏信息 |

---

## 9. 健康检查

所有服务默认开启 Spring Boot Actuator，提供以下端点：

```bash
# 检查服务健康状态
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health

# 预期响应:
# {"status":"UP"}
```

### Nacos 服务注册检查

登录 Nacos 控制台 http://localhost:8848/nacos → 服务列表，应看到 5 个服务实例：

```
novaflow-auth-service          ✅ UP
novaflow-video-service         ✅ UP
novaflow-recommendation-service ✅ UP
novaflow-feedback-service      ✅ UP
novaflow-gateway              ✅ UP
```

---

## 10. 常见问题排查

### Q1: 服务启动失败 — Bean 冲突 `ChatMemory`

**现象**: `BeanDefinitionOverrideException: Bean definition for 'chatMemory'`

**原因**: `SpringAIConfig` 和 `ChatMemoryConfig` 同时声明了 `@Primary ChatMemory` bean

**解决**: 已在最新版本修复。确保 `SpringAIConfig.java` 中没有 `chatMemory()` Bean 方法。只有 `ChatMemoryConfig` 中的 Redis 版本应该保留。

---

### Q2: DashScope 调用失败 — API Key 无效

**现象**: `401 Unauthorized` 或 `InvalidApiKey`

**排查**:
```bash
# 检查配置
grep "api-key" novaflow-recommendation-service/src/main/resources/application.yml

# 确认 API Key 有效
curl -X POST https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation \
  -H "Authorization: Bearer <你的API_KEY>" \
  -H "Content-Type: application/json" \
  -d '{"model":"qwen-max","input":{"messages":[{"role":"user","content":"test"}]}}'
```

---

### Q3: Nacos 连接失败

**现象**: `com.alibaba.nacos.api.exception.NacosException: failed to connect`

**排查**:
```bash
# 检查 Nacos 是否运行
curl http://localhost:8848/nacos/v1/ns/operator/leaders

# 检查 bootstrap.yml 中的地址配置
grep "server-addr" novaflow-*/src/main/resources/bootstrap.yml

# 如果 Nacos 不在本地，通过环境变量指定
export NACOS_SERVER_ADDR=your-nacos-host:8848
```

---

### Q4: RocketMQ 连接失败

**现象**: `org.apache.rocketmq.client.exception.MQClientException: connect to <host>:9876 failed`

**排查**:
```bash
# 检查 Name Server 是否运行
ps aux | grep mqnamesrv

# 检查端口是否开放
telnet localhost 9876
```

---

### Q5: 数据库连接失败

**现象**: `java.sql.SQLException: Access denied` 或 `Communications link failure`

**排查**:
```bash
# 检查 MySQL 是否运行
mysql -u novaflow -pnovaflow123 -h localhost -e "SELECT 1"

# 检查数据库是否存在
mysql -u root -p -e "SHOW DATABASES LIKE 'novaflow'"

# 检查用户权限
mysql -u root -p -e "SHOW GRANTS FOR 'novaflow'@'%'"
```

---

### Q6: 视频上传失败 — OSS 配置错误

**现象**: `com.aliyun.oss.OSSException: The OSS Access Key Id you provided does not exist`

**排查**: 确认 `application.yml` 中 `aliyun.oss` 的 `access-key-id`、`access-key-secret`、`endpoint`、`bucket-name` 配置正确。确认 OSS Bucket 已创建且权限设置为公共读。

---

### Q7: 微信登录失败

**现象**: `com.github.binarywang.wxminiapp.api.WxMaServiceImpl` 报错

**排查**: 确认 `app.wechat.app-id` 和 `app.wechat.app-secret` 与微信小程序后台一致。确认服务器 IP 已加入微信后台白名单。

---

### Q8: Agent 对话无记忆

**现象**: AI 不记得上一轮对话内容

**排查**:
1. 确认 `Redis` 正常运行（短期记忆依赖 Redis）
2. 确认请求中携带了 `sessionId`（每次对话使用相同的 sessionId）
3. 确认 `ChatMemoryConfig` 的 `chatMemory` Bean 正常注入
4. 查看 Redis 中的记忆数据：`redis-cli KEYS "chat:memory:*"`

---

## 附录 A: 开发环境快速启动脚本

```bash
#!/bin/bash
# quick-start.sh — NovaFlow-v2 开发环境一键启动

set -e

echo "=== NovaFlow-v2 开发环境启动 ==="

# 1. 检查基础设施
echo "[1/4] 检查 MySQL..."
mysql -u novaflow -pnovaflow123 -h localhost -e "SELECT 1" 2>/dev/null || {
    echo "❌ MySQL 未就绪"
    exit 1
}

echo "[2/4] 检查 Redis..."
redis-cli ping 2>/dev/null | grep -q PONG || {
    echo "❌ Redis 未就绪"
    exit 1
}

echo "[3/4] 检查 Nacos..."
curl -s http://localhost:8848/nacos/v1/ns/operator/leaders > /dev/null || {
    echo "❌ Nacos 未就绪"
    exit 1
}

echo "[4/4] 检查 RocketMQ..."
ps aux | grep -q "[m]qnamesrv" || {
    echo "⚠️  RocketMQ Name Server 未检测到，消息功能可能不可用"
}

# 2. 构建
echo ""
echo "=== 构建项目 ==="
mvn clean package -DskipTests -q

# 3. 启动服务
SERVICES=(
    "novaflow-auth-service:8081:AuthService"
    "novaflow-video-service:8082:VideoService"
    "novaflow-recommendation-service:8083:RecommendationService"
    "novaflow-feedback-service:8084:FeedbackService"
    "novaflow-gateway:8080:Gateway"
)

for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r service port name <<< "$service_info"
    JAR="target/${service}-2.0.0-SNAPSHOT.jar"
    echo "启动 ${name} (${service}) :${port}..."
    java -jar ${JAR} > /tmp/novaflow-${service}.log 2>&1 &
    sleep 5  # 等待服务注册到 Nacos
done

echo ""
echo "=== 全部服务已启动 ==="
echo "Gateway: http://localhost:8080"
echo "Nacos 控制台: http://localhost:8848/nacos"
echo "日志目录: /tmp/novaflow-*.log"
```

---

## 附录 B: 服务端口速查表

| 服务 | 端口 | 进程名 |
|------|------|--------|
| Nacos | 8848 | nacos |
| Nacos gRPC | 9848 | nacos |
| RocketMQ Name Server | 9876 | mqnamesrv |
| RocketMQ Broker | 10911 | mqbroker |
| MySQL | 3306 | mysqld |
| Redis | 6379 | redis-server |
| **Gateway** | **8080** | novaflow-gateway |
| **Auth Service** | **8081** | novaflow-auth-service |
| **Video Service** | **8082** | novaflow-video-service |
| **Recommendation Service** | **8083** | novaflow-recommendation-service |
| **Feedback Service** | **8084** | novaflow-feedback-service |

---

## 附录 C: RocketMQ Topic 清单

| Topic | 生产者 | 消费者 | 说明 |
|-------|--------|--------|------|
| `video-processing-topic` | video-service | video-service, recommendation-service | 视频处理事件流 |
| `recommendation-topic` | recommendation-service | recommendation-service, feedback-service | 推荐生成事件流 |

### 消费者组

| Consumer Group | 服务 | Topic |
|----------------|------|-------|
| `novaflow-video-producer-group` | video-service | video-processing-topic |
| `video-upload-consumer-group` | video-service | video-processing-topic |
| `video-analysis-consumer-group` | video-service | video-processing-topic |
| `novaflow-recommendation-consumer-group` | recommendation-service | recommendation-topic |
| `novaflow-feedback-producer-group` | feedback-service | recommendation-topic |
