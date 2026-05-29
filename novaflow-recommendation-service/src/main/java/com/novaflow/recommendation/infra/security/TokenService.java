package com.novaflow.recommendation.infra.security;

/**
 * 令牌服务接口（本地存根）
 * 来自 auth 服务的跨服务依赖
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
