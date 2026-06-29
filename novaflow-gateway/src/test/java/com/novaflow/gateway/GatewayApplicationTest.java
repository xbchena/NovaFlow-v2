package com.novaflow.gateway;

import com.novaflow.gateway.config.GatewayProperties;
import com.novaflow.gateway.security.JwtAuthenticationGlobalFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 配置加载冒烟测试：确保 application.yml 的 gateway.* 正确绑定到 GatewayProperties，
 * 且关键 Bean（JwtAuthenticationGlobalFilter、RateLimiterConfig 等）能被装配。
 * GatewayProperties 用显式 bean 名 novaflowGatewayProperties 以避免与 SCG 内置
 * GatewayProperties 同名冲突。
 */
@SpringBootTest
@ActiveProfiles("test")
class GatewayApplicationTest {

    @Autowired
    GatewayProperties gatewayProperties;

    @Autowired
    JwtAuthenticationGlobalFilter authFilter;

    @Test
    void propertiesBindedAndBeansPresent() {
        assertThat(gatewayProperties.getJwtSecret()).isNotBlank();
        assertThat(gatewayProperties.getWhitelist())
                .contains("/api/v1/auth/wechat/callback", "/api/v1/auth/refresh");
        assertThat(authFilter).isNotNull();
    }
}
