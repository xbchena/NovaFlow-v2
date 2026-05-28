package com.novaflow.auth.application.command;

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
