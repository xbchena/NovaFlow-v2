# NovaFlow 本地开发基础设施

启动：
    docker compose -f docker/nacos/docker-compose.yml up -d

- Nacos 控制台: http://localhost:8848/nacos  (默认账号 nacos/nacos)
- Redis: localhost:6379

启动后，在 Nacos 控制台 → 配置管理，新建 dataId=`novaflow-common.yaml`、group=`DEFAULT_GROUP`，
把各服务共享的 Redis/日志配置放进去（bootstrap.yml 已引用该 shared-config）。
