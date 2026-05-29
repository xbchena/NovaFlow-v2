package com.novaflow.video.infra.security;

/**
 * 令牌服务接口 (Phase 1 本地存根)
 * Phase 3 将通过 Feign 调用 auth-service
 */
public interface TokenService {

    /**
     * 生成访问令牌
     */
    String generateAccessToken(String userId);

    /**
     * 生成刷新令牌
     */
    String generateRefreshToken(String userId);

    /**
     * 验证访问令牌，返回用户ID
     */
    String validateAccessToken(String token);

    /**
     * 验证刷新令牌，返回用户ID
     */
    String validateRefreshToken(String token);
}
