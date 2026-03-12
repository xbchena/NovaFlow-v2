package com.novaflow.infrastructure.external.wechat;

/**
 * 微信认证服务
 * 基于 WxJava SDK 处理与微信API的交互
 */
public interface WeChatAuthService {

    /**
     * 使用授权码进行认证
     * 调用微信 code2Session 接口获取 openid 和 session_key
     *
     * @param code 微信小程序登录时获取的code
     * @return 认证结果，包含 openid、unionid、sessionKey
     */
    WeChatAuthResult authenticate(String code);

    /**
     * 解密用户信息
     * 使用 session_key 解密加密的用户数据
     *
     * @param sessionKey 会话密钥
     * @param encryptedData 加密的数据
     * @param ivStr 加密算法的初始向量
     * @return 解密后的用户信息
     */
    WeChatAuthResult getUserInfo(String sessionKey, String encryptedData, String ivStr);

    /**
     * 获取用户手机号
     * 使用微信手机号快速验证组件获取code后换取手机号
     *
     * @param code 手机号验证码
     * @return 用户手机号
     */
    String getPhoneNumber(String code);

    /**
     * 验证用户信息完整性
     * 检查用户数据是否被篡改
     *
     * @param sessionKey 会话密钥
     * @param rawDatas 原始数据
     * @param signature 数据签名
     * @return 验证是否通过
     */
    boolean checkUserInfo(String sessionKey, String rawDatas, String signature);

    /**
     * 获取小程序 access_token
     * 用于调用微信其他 API
     *
     * @return access_token
     */
    String getAccessToken();

    /**
     * 微信认证结果
     * 包含用户基本信息和会话信息
     */
    class WeChatAuthResult {
        private final String openid;
        private final String unionid;
        private final String sessionKey;
        private final String nickname;
        private final String headImgUrl;
        private final Integer sex;
        private final String country;
        private final String province;
        private final String city;
        private final String language;

        private WeChatAuthResult(String openid, String unionid, String sessionKey, String nickname,
                                String headImgUrl, Integer sex, String country, String province,
                                String city, String language) {
            this.openid = openid;
            this.unionid = unionid;
            this.sessionKey = sessionKey;
            this.nickname = nickname;
            this.headImgUrl = headImgUrl;
            this.sex = sex;
            this.country = country;
            this.province = province;
            this.city = city;
            this.language = language;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getOpenid() {
            return openid;
        }

        public String getUnionid() {
            return unionid;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public String getNickname() {
            return nickname;
        }

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public Integer getSex() {
            return sex;
        }

        public String getCountry() {
            return country;
        }

        public String getProvince() {
            return province;
        }

        public String getCity() {
            return city;
        }

        public String getLanguage() {
            return language;
        }

        public static class Builder {
            private String openid;
            private String unionid;
            private String sessionKey;
            private String nickname;
            private String headImgUrl;
            private Integer sex;
            private String country;
            private String province;
            private String city;
            private String language;

            public Builder openid(String openid) {
                this.openid = openid;
                return this;
            }

            public Builder unionid(String unionid) {
                this.unionid = unionid;
                return this;
            }

            public Builder sessionKey(String sessionKey) {
                this.sessionKey = sessionKey;
                return this;
            }

            public Builder nickname(String nickname) {
                this.nickname = nickname;
                return this;
            }

            public Builder headImgUrl(String headImgUrl) {
                this.headImgUrl = headImgUrl;
                return this;
            }

            public Builder sex(Integer sex) {
                this.sex = sex;
                return this;
            }

            public Builder country(String country) {
                this.country = country;
                return this;
            }

            public Builder province(String province) {
                this.province = province;
                return this;
            }

            public Builder city(String city) {
                this.city = city;
                return this;
            }

            public Builder language(String language) {
                this.language = language;
                return this;
            }

            public WeChatAuthResult build() {
                return new WeChatAuthResult(openid, unionid, sessionKey, nickname,
                        headImgUrl, sex, country, province, city, language);
            }
        }
    }
}
