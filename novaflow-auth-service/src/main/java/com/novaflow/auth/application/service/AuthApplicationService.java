package com.novaflow.auth.application.service;

import com.novaflow.auth.application.assembler.UserAssembler;
import com.novaflow.common.domain.event.DomainEvent;
import com.novaflow.auth.domain.model.auth.User;
import com.novaflow.auth.domain.model.auth.exception.InvalidCredentialsException;
import com.novaflow.auth.domain.model.auth.exception.InvalidTokenException;
import com.novaflow.auth.domain.model.auth.exception.UserNotFoundException;
import com.novaflow.auth.domain.model.auth.valueobject.OpenID;
import com.novaflow.auth.domain.model.auth.valueobject.PhoneNumber;
import com.novaflow.auth.domain.model.auth.valueobject.WeChatInfo;
import com.novaflow.common.domain.valueobject.UserId;
import com.novaflow.common.domain.repository.DomainEventPublisher;
import com.novaflow.auth.domain.repository.UserRepository;
import com.novaflow.auth.infra.external.sms.SmsService;
import com.novaflow.auth.infra.external.wechat.WeChatAuthService;
import com.novaflow.auth.infra.security.TokenService;
import com.novaflow.auth.interfaces.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 认证应用服务
 * 协调认证领域的用例编排
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;
    private final WeChatAuthService weChatAuthService;
    private final SmsService smsService;
    private final TokenService tokenService;

    /**
     * 微信登录
     */
    @Transactional
    public AuthResponse loginWithWeChat(String code) {
        // 1. 调用微信API获取用户信息
        WeChatAuthService.WeChatAuthResult authResult = weChatAuthService.authenticate(code);

        // 2. 查找或创建用户
        OpenID openid = OpenID.of(authResult.getOpenid());
        Optional<User> existingUser = userRepository.findByOpenID(openid);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            // 更新用户信息
            if (authResult.getUnionid() != null) {
                // 可以在这里更新UnionID等信息
            }
        } else {
            // 创建新用户
            WeChatInfo weChatInfo = WeChatInfo.builder()
                    .nickname(authResult.getNickname())
                    .avatar(authResult.getHeadImgUrl())
                    .gender(authResult.getSex())
                    .country(authResult.getCountry())
                    .province(authResult.getProvince())
                    .city(authResult.getCity())
                    .build();

            user = User.createViaWeChat(openid,
                    authResult.getUnionid() != null ? com.novaflow.auth.domain.model.auth.valueobject.UnionID.of(authResult.getUnionid()) : null,
                    weChatInfo);

            user = userRepository.save(user);
        }

        // 3. 发布领域事件
        publishDomainEvents(user);

        // 4. 生成令牌
        String accessToken = tokenService.generateAccessToken(user.getUserId().getValue());
        String refreshToken = tokenService.generateRefreshToken(user.getUserId().getValue());

        // 5. 返回响应
        return UserAssembler.toAuthResponse(user, accessToken, refreshToken);
    }

    /**
     * 手机号登录
     */
    @Transactional
    public AuthResponse loginWithPhone(String phone, String code) {
        // 1. 验证验证码
        if (!smsService.verifyCode(phone, code)) {
            throw new InvalidCredentialsException("验证码不正确或已过期");
        }

        // 2. 查找用户
        PhoneNumber phoneNumber = PhoneNumber.of(phone);
        Optional<User> existingUser = userRepository.findByPhone(phoneNumber);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // 创建新用户
            user = User.createViaPhone(phoneNumber, "用户" + phone.substring(7));
            user = userRepository.save(user);
        }

        // 3. 发布领域事件
        publishDomainEvents(user);

        // 4. 生成令牌
        String accessToken = tokenService.generateAccessToken(user.getUserId().getValue());
        String refreshToken = tokenService.generateRefreshToken(user.getUserId().getValue());

        // 5. 返回响应
        return UserAssembler.toAuthResponse(user, accessToken, refreshToken);
    }

    /**
     * 发送验证码
     */
    public void sendVerificationCode(String phone) {
        // 验证手机号格式
        if (!PhoneNumber.isValid(phone)) {
            throw new IllegalArgumentException("手机号格式不正确");
        }

        smsService.sendVerificationCode(phone);
    }

    /**
     * 刷新令牌
     */
    public AuthResponse refreshToken(String refreshToken) {
        // 1. 验证刷新令牌
        String userIdStr = tokenService.validateRefreshToken(refreshToken);
        if (userIdStr == null) {
            throw new InvalidTokenException("刷新令牌无效或已过期");
        }

        // 2. 查找用户
        UserId userId = UserId.of(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // 3. 生成新令牌
        String accessToken = tokenService.generateAccessToken(user.getUserId().getValue());
        String newRefreshToken = tokenService.generateRefreshToken(user.getUserId().getValue());

        // 4. 返回响应
        return UserAssembler.toAuthResponse(user, accessToken, newRefreshToken);
    }

    /**
     * 获取当前用户信息
     */
    public AuthResponse.UserInfo getCurrentUserInfo(String token) {
        // 1. 验证访问令牌
        String userIdStr = tokenService.validateAccessToken(token);
        if (userIdStr == null) {
            throw new InvalidTokenException("访问令牌无效或已过期");
        }

        // 2. 查找用户
        UserId userId = UserId.of(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // 3. 返回用户信息
        return UserAssembler.toUserInfo(user);
    }

    /**
     * 发布领域事件
     */
    private void publishDomainEvents(User user) {
        for (DomainEvent event : user.getDomainEvents()) {
            eventPublisher.publishAsync(event);
        }
        user.clearDomainEvents();
    }
}
