# 分库分表配置指南

## 概述

本系统使用 Apache ShardingSphere 实现分库分表功能，支持水平扩展以应对大量用户和数据。

## 配置说明

### 1. 分片策略

- **分库数量**: 2个数据库 (novaflow_0, novaflow_1)
- **分表数量**: 每库4张表 (user_0, user_1, user_2, user_3)
- **总分片数**: 8个物理表

### 2. 分片规则

| 表名 | 分片键 | 数据库分片算法 | 表分片算法 |
|------|--------|----------------|------------|
| user | id | id % 2 | id % 4 |
| video | user_id | user_id % 2 | user_id % 4 |
| recommendation | user_id | user_id % 2 | user_id % 4 |
| user_selection | user_id | user_id % 2 | user_id % 4 |

## 启用分库分表

### 方法1: 修改配置文件

在 `application.yml` 中设置：

```yaml
spring:
  shardingsphere:
    enabled: true
```

### 方法2: 启动参数

```bash
java -jar novaflow-backend.jar --spring.shardingsphere.enabled=true
```

## 数据库准备

### 1. 创建数据库

```sql
CREATE DATABASE novaflow_0 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE novaflow_1 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 创建分表

在每个数据库中执行以下建表语句：

```sql
-- 用户表
CREATE TABLE user_0 (
    id BIGINT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_1 (
    -- 同上
);

CREATE TABLE user_2 (
    -- 同上
);

CREATE TABLE user_3 (
    -- 同上
);

-- 视频表
CREATE TABLE video_0 (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'processing',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- video_1, video_2, video_3 类似

-- 推荐表
CREATE TABLE recommendation_0 (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content JSON NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- recommendation_1, recommendation_2, recommendation_3 类似

-- 用户选择记录表
CREATE TABLE user_selection_0 (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL,
    selected_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- user_selection_1, user_selection_2, user_selection_3 类似
```

## ID 生成策略

使用雪花算法生成分布式唯一ID：

```java
@Autowired
private SnowflakeIdGenerator idGenerator;

// 生成用户ID
Long userId = idGenerator.generateUserId();

// 生成视频ID
Long videoId = idGenerator.generateVideoId();
```

## 分片路由计算

```java
// 计算分片信息
ShardingConfig.ShardingInfo info = ShardingConfig.calculateShardingInfo(userId);

// 获取数据源
String dataSource = info.getDataSourceName(); // "ds0" or "ds1"

// 获取实际表名
String tableName = info.getTableName("user"); // "user_0" to "user_3"
```

## 注意事项

1. **跨分片查询**: 尽量避免跨分片的JOIN操作，应在应用层处理
2. **分片键选择**: 所有查询都应带上分片键，避免全路由扫描
3. **扩容**: 当前采用取模算法，扩容需要数据迁移
4. **事务**: ShardingSphere 支持分布式事务，但性能有损耗，谨慎使用

## 从单库迁移到分库分表

1. 备份现有数据
2. 创建新的分库分表结构
3. 编写数据迁移脚本，按分片规则分配数据
4. 验证数据完整性
5. 切换到分库分表模式
6. 灰度验证
7. 正式上线

## 监控建议

1. 监控各分片的访问量和数据量分布
2. 监控慢查询，优化跨分片查询
3. 定期检查数据分布是否均匀
