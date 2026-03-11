package com.novaflow.infrastructure.external.wechat;

import lombok.Builder;
import lombok.Data;

/**
 * 微信认证服务
 * 处理与微信API的交互
 */
public interface WeChatAuthService {

    /**
     * 使用授权码进行认证
     */
    WeChatAuthResult authenticate(String code);

    /**
     * 微信认证结果
     */
    @Data
    @Builder
    class WeChatAuthResult {
        private String openid;
        private String unionid;
        private String nickname;
        private String headImgUrl;
        private Integer sex;
        private String country;
        private String province;
        private String city;
    }
}
