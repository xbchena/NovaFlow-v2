package com.novaflow.application.command.auth;

import lombok.Builder;
import lombok.Getter;

/**
 * 微信登录命令
 */
@Getter
@Builder
public class WeChatLoginCommand {
    private final String code;
    private final String state;
}
