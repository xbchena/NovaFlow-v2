package com.novaflow.infrastructure.external.wechat;

import com.novaflow.infrastructure.config.WeChatConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 微信认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeChatAuthServiceImpl implements WeChatAuthService {

    private static final Logger log = LoggerFactory.getLogger(WeChatAuthServiceImpl.class);

    private final WeChatConfig weChatConfig;
    private final RestTemplate restTemplate;

    @Override
    public WeChatAuthResult authenticate(String code) {
        // TODO: 实现实际的微信API调用
        // 1. 使用code换取access_token和openid
        // 2. 使用access_token获取用户信息

        log.info("微信认证: code={}", code);

        // 临时实现：返回模拟数据
        return WeChatAuthResult.builder()
                .openid("mock_openid_" + code.substring(0, 8))
                .unionid(null)
                .nickname("微信用户")
                .headImgUrl("")
                .sex(0)
                .country("中国")
                .province("北京")
                .city("北京")
                .build();
    }
}
