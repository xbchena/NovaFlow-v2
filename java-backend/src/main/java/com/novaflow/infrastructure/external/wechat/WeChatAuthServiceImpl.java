package com.novaflow.infrastructure.external.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 微信认证服务实现
 * 基于 WxJava SDK 实现
 */
@Service
@RequiredArgsConstructor
public class WeChatAuthServiceImpl implements WeChatAuthService {

    private static final Logger log = LoggerFactory.getLogger(WeChatAuthServiceImpl.class);

    private final WxMaService wxMaService;

    @Override
    public WeChatAuthResult authenticate(String code) {
        try {
            log.info("开始微信认证: code={}", code);

            // 1. 使用code换取session信息
            WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(code);

            if (session == null) {
                throw new RuntimeException("微信登录失败：无法获取session信息");
            }

            log.info("微信认证成功: openid={}, unionid={}",
                    session.getOpenid(), session.getUnionid());

            // 2. 返回认证结果
            return WeChatAuthResult.builder()
                    .openid(session.getOpenid())
                    .unionid(session.getUnionid())
                    .sessionKey(session.getSessionKey())
                    .build();

        } catch (Exception e) {
            log.error("微信认证失败: code={}, error={}", code, e.getMessage(), e);
            throw new RuntimeException("微信认证失败: " + e.getMessage(), e);
        }
    }

    @Override
    public WeChatAuthResult getUserInfo(String sessionKey, String encryptedData, String ivStr) {
        try {
            log.info("开始解密用户信息");

            // 解密用户信息
            WxMaUserInfo userInfo = wxMaService.getUserService()
                    .getUserInfo(sessionKey, encryptedData, ivStr);

            if (userInfo == null) {
                throw new RuntimeException("解密用户信息失败");
            }

            log.info("解密用户信息成功: nickname={}", userInfo.getNickName());

            // 将 String 类型的性别转换为 Integer
            Integer genderValue = null;
            if (userInfo.getGender() != null && !userInfo.getGender().isEmpty()) {
                try {
                    genderValue = Integer.parseInt(userInfo.getGender());
                } catch (NumberFormatException e) {
                    log.warn("无法解析性别值: {}", userInfo.getGender());
                }
            }

            return WeChatAuthResult.builder()
                    .openid(null) // openid 从认证接口获取，不包含在用户信息中
                    .nickname(userInfo.getNickName())
                    .headImgUrl(userInfo.getAvatarUrl())
                    .sex(genderValue)
                    .country(userInfo.getCountry())
                    .province(userInfo.getProvince())
                    .city(userInfo.getCity())
                    .language(userInfo.getLanguage())
                    .build();

        } catch (Exception e) {
            log.error("解密用户信息失败: error={}", e.getMessage(), e);
            throw new RuntimeException("解密用户信息失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getPhoneNumber(String code) {
        try {
            log.info("开始获取手机号: code={}", code);

            // 获取手机号信息
            WxMaPhoneNumberInfo phoneInfo = wxMaService.getUserService()
                    .getPhoneNumber(code);

            if (phoneInfo == null) {
                throw new RuntimeException("获取手机号失败");
            }

            log.info("获取手机号成功: phoneNumber={}", phoneInfo.getPhoneNumber());

            return phoneInfo.getPhoneNumber();

        } catch (Exception e) {
            log.error("获取手机号失败: code={}, error={}", code, e.getMessage(), e);
            throw new RuntimeException("获取手机号失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean checkUserInfo(String sessionKey, String rawData, String signature) {
        try {
            return wxMaService.getUserService()
                    .checkUserInfo(sessionKey, rawData, signature);
        } catch (Exception e) {
            log.error("验证用户信息失败: error={}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getAccessToken() {
        try {
            return wxMaService.getAccessToken();
        } catch (Exception e) {
            log.error("获取access_token失败: error={}", e.getMessage(), e);
            throw new RuntimeException("获取access_token失败: " + e.getMessage(), e);
        }
    }
}
