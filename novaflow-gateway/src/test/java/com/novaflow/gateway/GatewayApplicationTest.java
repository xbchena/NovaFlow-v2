package com.novaflow.gateway;

import com.novaflow.gateway.config.GatewayProperties;
import com.novaflow.gateway.security.JwtAuthenticationGlobalFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 配置加载冒烟测试：确保 application.yml 的 gateway.* 正确绑定，且关键 Bean 装配成功。
 *
 * <p>使用 test profile（见 src/test/resources/application-test.yml）：
 * 全量上下文启动时，Spring Cloud Gateway 的内置 {@code gatewayProperties} bean 会与本项目
 * {@code @Component GatewayProperties} 同名冲突，且 Nacos 服务发现会尝试连接本地 Nacos。
 * test profile 开启 bean 覆盖并禁用 Nacos 注册，使测试在无外部依赖时也能加载上下文。
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
