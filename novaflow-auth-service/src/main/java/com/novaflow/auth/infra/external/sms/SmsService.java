package com.novaflow.auth.infra.external.sms;

/**
 * 短信服务接口
 * 处理验证码发送和验证
 */
public interface SmsService {

    /**
     * 发送验证码
     */
    void sendVerificationCode(String phone);

    /**
     * 验证验证码
     */
    boolean verifyCode(String phone, String code);
}
