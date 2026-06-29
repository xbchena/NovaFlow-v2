package com.novaflow.gateway.config;

import com.novaflow.gateway.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewaySecurityConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider(GatewayProperties properties) {
        return new JwtTokenProvider(properties.getJwtSecret());
    }
}
