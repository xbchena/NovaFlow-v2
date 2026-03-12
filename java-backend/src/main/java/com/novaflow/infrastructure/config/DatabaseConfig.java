package com.novaflow.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库配置类
 * 配置MyBatis Plus、分页和分库分表支持
 */
@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    /**
     * MyBatis Plus 拦截器配置
     * 添加分页插件支持
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(500L);  // 设置最大单页限制数量
        paginationInterceptor.setOverflow(false);  // 溢出总页数后是否进行处理

        interceptor.addInnerInterceptor(paginationInterceptor);

        log.info("MybatisPlus interceptor configured with pagination support");
        return interceptor;
    }

    /**
     * 分库分表配置类
     * 当启用分库分表时自动加载
     */
    @Configuration
    @ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true")
    public static class ShardingConfiguration {

        public ShardingConfiguration() {
            log.info("========== 分库分表已启用 ==========");
            log.info("数据库分片数: {}", ShardingConfig.DB_COUNT);
            log.info("每库表分片数: {}", ShardingConfig.TABLE_COUNT);
            log.info("总分片表数: {}", ShardingConfig.DB_COUNT * ShardingConfig.TABLE_COUNT);
            log.info("=================================");
        }
    }

    /**
     * 单库模式配置类
     * 当未启用分库分表时使用
     */
    @Configuration
    @ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "false", matchIfMissing = true)
    public static class SingleDatabaseConfiguration {

        public SingleDatabaseConfiguration() {
            log.info("使用单库模式，如需启用分库分表请在application.yml中设置 spring.shardingsphere.enabled=true");
        }
    }
}
