package com.novaflow.video.infra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 异步配置类
 * 使用 JDK 21 虚拟线程提升并发性能
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    /**
     * 配置虚拟线程执行器
     * JDK 21 的虚拟线程可以创建大量轻量级线程，显著提升并发性能
     */
    @Bean(name = "taskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadFactory virtualThreadFactory = Thread.ofVirtual()
                .name("async-virtual-", 0)
                .factory();

        log.info("Initialized virtual thread executor for async processing");
        return Executors.newThreadPerTaskExecutor(virtualThreadFactory);
    }

    /**
     * 配置传统线程池执行器（用于不支持虚拟线程的场景）
     */
    @Bean(name = "traditionalTaskExecutor")
    public Executor traditionalTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("traditional-async-");
        executor.setRejectedExecutionHandler((r, executor1) -> {
            log.warn("Task rejected, executing in caller thread");
            if (!executor1.isShutdown()) {
                r.run();
            }
        });
        executor.initialize();
        return executor;
    }

    /**
     * 异步异常处理器
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                log.error("Async method execution error: method={}, params={}, error={}",
                        method.getName(), params, ex.getMessage(), ex);
            }
        };
    }
}
