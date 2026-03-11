package com.novaflow.interfaces.rest;

import com.novaflow.application.service.AuthApplicationService;
import com.novaflow.interfaces.dto.response.Result;
import com.novaflow.interfaces.dto.request.LoginRequest;
import com.novaflow.interfaces.dto.request.WeChatCallbackRequest;
import com.novaflow.interfaces.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户认证相关的HTTP请求
 */
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;

    /**
     * 微信登录回调
     */
    @Operation(summary = "微信登录", description = "处理微信授权回调，完成用户登录")
    @PostMapping("/wechat/callback")
    public Result<AuthResponse> weChatCallback(@Valid @RequestBody WeChatCallbackRequest request) {
        AuthResponse response = authApplicationService.loginWithWeChat(request.getCode());
        return Result.ok(response);
    }

    /**
     * 手机号登录
     */
    @Operation(summary = "手机号登录", description = "使用手机号和验证码登录")
    @PostMapping("/phone/login")
    public Result<AuthResponse> phoneLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authApplicationService.loginWithPhone(request.getPhone(), request.getCode());
        return Result.ok(response);
    }

    /**
     * 发送验证码
     */
    @Operation(summary = "发送验证码", description = "发送手机验证码")
    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestParam String phone) {
        authApplicationService.sendVerificationCode(phone);
        return Result.ok();
    }

    /**
     * 刷新令牌
     */
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh")
    public Result<AuthResponse> refresh(@RequestParam String refreshToken) {
        AuthResponse response = authApplicationService.refreshToken(refreshToken);
        return Result.ok(response);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/user/info")
    public Result<AuthResponse.UserInfo> getCurrentUser(@RequestHeader("Authorization") String token) {
        // 移除 "Bearer " 前缀
        String tokenValue = token.replace("Bearer ", "");
        AuthResponse.UserInfo userInfo = authApplicationService.getCurrentUserInfo(tokenValue);
        return Result.ok(userInfo);
    }
}
