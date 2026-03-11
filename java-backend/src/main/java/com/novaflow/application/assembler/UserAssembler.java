package com.novaflow.application.assembler;

import com.novaflow.domain.model.auth.User;
import com.novaflow.interfaces.dto.response.AuthResponse;

/**
 * 用户聚合转换器
 * 负责领域对象与DTO之间的转换
 */
public class UserAssembler {

    /**
     * 转换为认证响应
     */
    public static AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(toUserInfo(user))
                .build();
    }

    /**
     * 转换为用户信息
     */
    public static AuthResponse.UserInfo toUserInfo(User user) {
        return AuthResponse.UserInfo.builder()
                .id(Long.valueOf(user.getUserId().getValue())) // 临时处理，可能需要调整
                .openid(user.getOpenid() != null ? user.getOpenid().getValue() : null)
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone() != null ? user.getPhone().getValue() : null)
                .build();
    }
}
