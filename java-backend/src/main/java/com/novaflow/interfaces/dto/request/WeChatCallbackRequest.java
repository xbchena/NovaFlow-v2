package com.novaflow.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录回调请求DTO
 */
@Data
public class WeChatCallbackRequest {

    /**
     * 微信授权码
     */
    @NotBlank(message = "授权码不能为空")
    private String code;

    /**
     * 状态码
     */
    private String state;

    /**
     * 获取授权码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取状态码
     */
    public String getState() {
        return state;
    }

    /**
     * 设置授权码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 设置状态码
     */
    public void setState(String state) {
        this.state = state;
    }
}
