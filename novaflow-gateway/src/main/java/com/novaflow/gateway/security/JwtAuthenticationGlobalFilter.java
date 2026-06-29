package com.novaflow.gateway.security;

import com.novaflow.gateway.config.GatewayProperties;
import com.novaflow.gateway.error.GatewayErrorWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationGlobalFilter.class);
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final String BEARER = "Bearer ";
    public static final String USER_ID_HEADER = "X-User-Id";

    private final JwtTokenProvider tokenProvider;
    private final ReactiveStringRedisTemplate redis;
    private final GatewayProperties properties;
    private final GatewayErrorWriter errorWriter;

    public JwtAuthenticationGlobalFilter(JwtTokenProvider tokenProvider,
                                         ReactiveStringRedisTemplate redis,
                                         GatewayProperties properties,
                                         GatewayErrorWriter errorWriter) {
        this.tokenProvider = tokenProvider;
        this.redis = redis;
        this.properties = properties;
        this.errorWriter = errorWriter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            return errorWriter.write(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "缺少认证令牌");
        }
        String token = authHeader.substring(BEARER.length()).trim();
        String userId = tokenProvider.parseAndValidate(token);
        if (userId == null) {
            return errorWriter.write(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "认证令牌无效或已过期");
        }

        String blacklistKey = properties.getBlacklistPrefix() + ":" + token;
        return redis.hasKey(blacklistKey)
                .defaultIfEmpty(false)
                .flatMap(blacklisted -> {
                    if (Boolean.TRUE.equals(blacklisted)) {
                        return errorWriter.write(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "令牌已注销");
                    }
                    ServerHttpRequest mutated = exchange.getRequest().mutate()
                            .header(USER_ID_HEADER, userId)
                            .build();
                    return chain.filter(exchange.mutate().request(mutated).build());
                });
    }

    private boolean isWhitelisted(String path) {
        List<String> whitelist = properties.getWhitelist();
        if (whitelist == null) return false;
        return whitelist.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
