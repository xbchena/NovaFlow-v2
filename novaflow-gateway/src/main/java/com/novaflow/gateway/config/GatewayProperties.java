package com.novaflow.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关层可配置项：JWT secret、白名单路径、黑名单 Redis key 前缀。
 */
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    /** 与 auth-service 共享的 HMAC secret，必须 >= 32 字节 */
    private String jwtSecret;
    /** 免鉴权路径（Ant 风格由过滤器用 PathMatcher 处理），存精确/前缀路径 */
    private List<String> whitelist = new ArrayList<>();
    /** Redis 中黑名单 token 的 key 前缀，完整 key = blacklistPrefix + ":" + token */
    private String blacklistPrefix = "auth:blacklist";

    public String getJwtSecret() { return jwtSecret; }
    public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }

    public List<String> getWhitelist() { return whitelist; }
    public void setWhitelist(List<String> whitelist) { this.whitelist = whitelist; }

    public String getBlacklistPrefix() { return blacklistPrefix; }
    public void setBlacklistPrefix(String blacklistPrefix) { this.blacklistPrefix = blacklistPrefix; }
}
