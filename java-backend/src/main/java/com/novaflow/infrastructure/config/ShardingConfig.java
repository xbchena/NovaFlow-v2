package com.novaflow.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 分库分表配置类
 * 当启用分库分表时生效
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ShardingConfig {

    // 分库分表配置常量
    public static final int DB_COUNT = 2;  // 数据库数量
    public static final int TABLE_COUNT = 4;  // 每个数据库的表数量

    /**
     * 计算数据源索引
     * @param shardingKey 分片键
     * @return 数据源索引 (0 或 1)
     */
    public static int calculateDbIndex(Long shardingKey) {
        if (shardingKey == null) {
            throw new IllegalArgumentException("分片键不能为空");
        }
        return (int) (shardingKey % DB_COUNT);
    }

    /**
     * 计算表索引
     * @param shardingKey 分片键
     * @return 表索引 (0-3)
     */
    public static int calculateTableIndex(Long shardingKey) {
        if (shardingKey == null) {
            throw new IllegalArgumentException("分片键不能为空");
        }
        return (int) (shardingKey % TABLE_COUNT);
    }

    /**
     * 获取实际表名
     * @param baseTableName 基础表名（如 user）
     * @param shardingKey 分片键
     * @return 实际表名（如 user_0）
     */
    public static String getActualTableName(String baseTableName, Long shardingKey) {
        int tableIndex = calculateTableIndex(shardingKey);
        return baseTableName + "_" + tableIndex;
    }

    /**
     * 计算数据源名称
     * @param shardingKey 分片键
     * @return 数据源名称（如 ds0, ds1）
     */
    public static String getDataSourceName(Long shardingKey) {
        int dbIndex = calculateDbIndex(shardingKey);
        return "ds" + dbIndex;
    }

    /**
     * 根据用户ID计算分片位置
     * 用于生成用户ID时的分片计算
     * @param userId 用户ID
     * @return 分片信息
     */
    public static ShardingInfo calculateShardingInfo(Long userId) {
        int dbIndex = calculateDbIndex(userId);
        int tableIndex = calculateTableIndex(userId);
        return new ShardingInfo(dbIndex, tableIndex);
    }

    /**
     * 分片信息类
     */
    public static class ShardingInfo {
        private final int dbIndex;
        private final int tableIndex;

        public ShardingInfo(int dbIndex, int tableIndex) {
            this.dbIndex = dbIndex;
            this.tableIndex = tableIndex;
        }

        public int getDbIndex() {
            return dbIndex;
        }

        public int getTableIndex() {
            return tableIndex;
        }

        public String getDataSourceName() {
            return "ds" + dbIndex;
        }

        public String getTableName(String baseTableName) {
            return baseTableName + "_" + tableIndex;
        }

        @Override
        public String toString() {
            return "ShardingInfo{dbIndex=" + dbIndex + ", tableIndex=" + tableIndex + "}";
        }
    }
}
