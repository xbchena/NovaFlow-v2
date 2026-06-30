# NovaFlow 阿里云部署指南

## 1. 本地已验证环境

当前本地依赖均在 Docker 中运行：

- MySQL: `localhost:3306`, database/user/password: `novaflow/novaflow/novaflow123`
- Redis: `localhost:6379`，当前复用了已有容器 `pay`
- Nacos: `localhost:8848`
- RocketMQ NameServer: `localhost:9876`
- RocketMQ Broker: `localhost:10911`

本地启动依赖：

```bash
cd /Users/chenxiangbing/Developer/NovaFlow-v2/docker/nacos
docker compose up -d mysql nacos rocketmq-namesrv rocketmq-broker
```

本机如果没有 Redis，且 6379 未被占用，可同时启动 compose 里的 `redis`；如果 6379 已被其他 Redis 占用，保持复用即可。

后端构建：

```bash
cd /Users/chenxiangbing/Developer/NovaFlow-v2
mvn test
mvn -DskipTests package
```

后端健康检查：

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
```

前端 Web 调试：

```bash
cd /Users/chenxiangbing/Developer/NovaFlow-v2/mobile-app
npx expo install react-native-web react-dom @expo/metro-runtime
CI=1 npm run web -- --port 19006
```

## 2. 推荐阿里云架构

小规模先用 ECS + Docker Compose，少折腾、上线快：

- ECS: 2 核 4G 起步，安装 Docker 和 Docker Compose
- RDS MySQL 8.0: 生产数据库
- Redis/Tair: 生产缓存
- Nacos: 优先用 MSE Nacos；预算敏感可先 ECS 自建
- RocketMQ: 优先用阿里云消息队列 RocketMQ；预算敏感可先 ECS 自建
- OSS: 视频文件存储
- ALB/SLB: 对外只暴露 gateway `8080` 或 Nginx `80/443`
- ACR: 保存后端镜像
- 日志服务 SLS: 收集 Java 日志

安全组只开放：

- `80/443`: 对公网
- `22`: 仅你的办公 IP
- `8080`: 如不用 Nginx，可临时开放；生产建议只走 `443`
- MySQL、Redis、Nacos、RocketMQ 端口不要对公网开放

## 3. 生产环境变量

所有服务至少准备：

```bash
SPRING_PROFILES_ACTIVE=prod
NACOS_SERVER_ADDR=<mse-nacos-host>:8848
NOVAFLOW_JWT_SECRET=<至少32字节强随机字符串>
MYSQL_HOST=<rds-host>
MYSQL_PORT=3306
MYSQL_DATABASE=novaflow
MYSQL_USERNAME=<rds-user>
MYSQL_PASSWORD=<rds-password>
REDIS_HOST=<redis-host>
REDIS_PORT=6379
ROCKETMQ_NAME_SERVER=<rocketmq-namesrv>
```

按功能再补：

```bash
DASHSCOPE_API_KEY=<通义千问/百炼 key>
AMAP_API_KEY=<高德 key>
ALIYUN_OSS_ENDPOINT=<oss endpoint>
ALIYUN_OSS_BUCKET=<bucket>
ALIYUN_ACCESS_KEY_ID=<ram access key>
ALIYUN_ACCESS_KEY_SECRET=<ram secret>
WECHAT_APP_ID=<微信 app id>
WECHAT_APP_SECRET=<微信 app secret>
```

移动端/Expo：

```bash
EXPO_PUBLIC_API_BASE_URL=https://api.your-domain.com/api/v1
EXPO_PUBLIC_WECHAT_APP_ID=<微信 app id>
```

## 4. ECS + Docker Compose 上线步骤

1. 创建 RDS 数据库 `novaflow`，导入项目 SQL。
2. 创建 Redis/Tair 实例。
3. 准备 Nacos 和 RocketMQ。生产优先用托管版；自建时可参考 `docker/nacos/docker-compose.yml`。
4. 在 ECS 安装 Docker：

```bash
sudo yum update -y
sudo yum install -y docker git
sudo systemctl enable --now docker
```

5. 构建后端 jar：

```bash
git clone <repo-url> /opt/novaflow
cd /opt/novaflow
mvn -DskipTests package
```

6. 启动顺序：

```bash
java -jar novaflow-auth-service/target/novaflow-auth-service-2.0.0-SNAPSHOT.jar
java -jar novaflow-video-service/target/novaflow-video-service-2.0.0-SNAPSHOT.jar
java -jar novaflow-recommendation-service/target/novaflow-recommendation-service-2.0.0-SNAPSHOT.jar
java -jar novaflow-feedback-service/target/novaflow-feedback-service-2.0.0-SNAPSHOT.jar
java -jar novaflow-gateway/target/novaflow-gateway-2.0.0-SNAPSHOT.jar
```

生产建议用 systemd 或 Docker 镜像托管这些 jar，不要裸跑在终端。

7. 对外只代理 gateway：

```nginx
server {
  listen 80;
  server_name api.your-domain.com;

  location / {
    proxy_pass http://127.0.0.1:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }
}
```

8. 验证：

```bash
curl https://api.your-domain.com/actuator/health
curl https://api.your-domain.com/api/v1/auth/health
```

## 5. 上 ACK 的处理

服务规模起来后再迁移 ACK：

- 每个 Spring Boot 模块一个 Deployment
- gateway 一个 Service + Ingress
- 配置放 ConfigMap，密钥放 Secret
- 镜像推 ACR
- 数据库用 RDS，Redis 用 Tair，RocketMQ 用阿里云托管版，Nacos 用 MSE
- readiness/liveness 都用 `/actuator/health`

最小发布顺序：

1. `novaflow-auth-service`
2. `novaflow-video-service`
3. `novaflow-recommendation-service`
4. `novaflow-feedback-service`
5. `novaflow-gateway`

## 6. 当前上线前必须确认

- 把所有默认密钥换掉，尤其是 `NOVAFLOW_JWT_SECRET`
- 导入完整 MySQL 表结构；目前本地只验证了服务启动和健康检查
- 替换本地调试用的 OSS、视频分析、跨服务查询适配器为生产实现
- 微信、DashScope、高德、OSS 必须配置真实 key
- Nacos 配置中心目前允许空配置启动；生产建议把公共配置沉到 Nacos
- 为 RocketMQ topic 和 consumer group 建立生产命名规范
