package com.novaflow.auth.infra.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置类
 * 配置 WxMaService Bean
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wx.miniapp.apps.default")
public class WxMaConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WxMaConfiguration.class);

    /**
     * 微信小程序 AppID
     */
    private String appId;

    /**
     * 微信小程序 AppSecret
     */
    private String appSecret;

    /**
     * 微信小程序 Token（用于消息推送）
     */
    private String token;

    /**
     * 微信小程序 AES Key（用于消息加密）
     */
    private String aesKey;

    /**
     * 配置 WxMaService Bean
     */
    @Bean
    public WxMaService wxMaService() {
        log.info("初始化微信小程序服务: appId={}", appId);

        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(appId);
        config.setSecret(appSecret);
        config.setToken(token);
        config.setAesKey(aesKey);

        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);

        log.info("微信小程序服务初始化完成");
        return service;
    }
}
