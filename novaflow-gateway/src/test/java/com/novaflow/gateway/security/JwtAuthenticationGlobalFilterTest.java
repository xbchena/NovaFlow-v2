package com.novaflow.gateway.security;

import com.novaflow.gateway.config.GatewayProperties;
import com.novaflow.gateway.error.GatewayErrorWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JwtAuthenticationGlobalFilterTest {

    private static final String SECRET = "novaflow-jwt-secret-change-me-please-256bit-key";
    private JwtTokenProvider provider;
    private ReactiveStringRedisTemplate redis;
    private GatewayProperties properties;
    private GatewayErrorWriter errorWriter;
    private JwtAuthenticationGlobalFilter filter;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET);
        redis = mock(ReactiveStringRedisTemplate.class);
        when(redis.hasKey(anyString())).thenReturn(Mono.just(false));
        properties = new GatewayProperties();
        properties.setJwtSecret(SECRET);
        properties.setWhitelist(List.of("/api/v1/auth/wechat/callback"));
        properties.setBlacklistPrefix("auth:blacklist");
        errorWriter = new GatewayErrorWriter();
        filter = new JwtAuthenticationGlobalFilter(provider, redis, properties, errorWriter);
    }

    @Test
    void whitelistPath_passesThrough_withoutAuthHeader() {
        MockServerHttpRequest req = MockServerHttpRequest.post("/api/v1/auth/wechat/callback").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(req);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        verify(chain).filter(exchange);
    }

    @Test
    void protectedPath_missingToken_returns401() {
        MockServerHttpRequest req = MockServerHttpRequest.get("/api/v1/user/info").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(req);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        StepVerifier.create(filter.filter(exchange, chain)).expectComplete().verify();
        verifyNoInteractions(chain);
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void protectedPath_invalidToken_returns401() {
        MockServerHttpRequest req = MockServerHttpRequest.get("/api/v1/user/info")
                .header("Authorization", "Bearer not-a-real-token").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(req);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        StepVerifier.create(filter.filter(exchange, chain)).expectComplete().verify();
        verifyNoInteractions(chain);
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void protectedPath_blacklistedToken_returns401() {
        String token = validToken("user-9");
        when(redis.hasKey(eq("auth:blacklist:" + token))).thenReturn(Mono.just(true));

        MockServerHttpRequest req = MockServerHttpRequest.get("/api/v1/user/info")
                .header("Authorization", "Bearer " + token).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(req);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        StepVerifier.create(filter.filter(exchange, chain)).expectComplete().verify();
        verifyNoInteractions(chain);
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void protectedPath_validToken_injectsUserIdHeader_andProceeds() {
        String token = validToken("user-42");
        MockServerHttpRequest req = MockServerHttpRequest.get("/api/v1/user/info")
                .header("Authorization", "Bearer " + token).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(req);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        ServerHttpRequest mutated = exchange.getRequest();
        assert mutated.getHeaders().getFirst("X-User-Id").equals("user-42");
        verify(chain).filter(any());
    }

    private String validToken(String userId) {
        try {
            var keyField = JwtTokenProvider.class.getDeclaredField("signingKey");
            keyField.setAccessible(true);
            javax.crypto.SecretKey key = (javax.crypto.SecretKey) keyField.get(provider);
            java.util.Date now = new java.util.Date();
            return io.jsonwebtoken.Jwts.builder()
                    .subject(userId)
                    .issuedAt(now)
                    .expiration(new java.util.Date(now.getTime() + 60_000))
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
