package com.novaflow.recommendation.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 外部服务配置类
 * 配置访问外部API所需的客户端
 */
@Configuration
public class ExternalServiceConfig {

    /**
     * 配置RestTemplate Bean
     * 用于调用微信API、阿里云API等外部服务
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 连接超时5秒
        factory.setReadTimeout(10000);    // 读取超时10秒
        return new RestTemplate(factory);
    }
}
