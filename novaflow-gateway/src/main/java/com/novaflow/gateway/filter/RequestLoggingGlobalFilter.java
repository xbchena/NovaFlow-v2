package com.novaflow.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 为每个请求生成唯一 traceId，注入请求头（X-Trace-Id）和响应头，
 * 并记录方法、路径、状态码、耗时。
 */
@Component
public class RequestLoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingGlobalFilter.class);
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        long start = System.currentTimeMillis();

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header(TRACE_ID_HEADER, traceId)
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutated).build();
        mutatedExchange.getResponse().getHeaders().add(TRACE_ID_HEADER, traceId);

        return chain.filter(mutatedExchange)
                .doOnError(e -> log.error("[traceId={}] 请求异常 path={} err={}",
                        traceId, mutatedExchange.getRequest().getURI().getPath(), e.getMessage()))
                .doFinally(signal -> {
                    int status = mutatedExchange.getResponse().getStatusCode() != null
                            ? mutatedExchange.getResponse().getStatusCode().value() : 0;
                    log.info("[traceId={}] {} {} -> {} ({}ms)",
                            traceId,
                            mutatedExchange.getRequest().getMethod(),
                            mutatedExchange.getRequest().getURI().getPath(),
                            status,
                            System.currentTimeMillis() - start);
                });
    }

    @Override
    public int getOrder() {
        return -300; // 比鉴权过滤器(-200)更早执行，保证鉴权失败也有 traceId
    }
}
