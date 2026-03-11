package com.novaflow.infrastructure.external.wechat;

import lombok.Builder;

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
    class WeChatAuthResult {
        private final String openid;
        private final String unionid;
        private final String nickname;
        private final String headImgUrl;
        private final Integer sex;
        private final String country;
        private final String province;
        private final String city;

        private WeChatAuthResult(String openid, String unionid, String nickname, String headImgUrl,
                               Integer sex, String country, String province, String city) {
            this.openid = openid;
            this.unionid = unionid;
            this.nickname = nickname;
            this.headImgUrl = headImgUrl;
            this.sex = sex;
            this.country = country;
            this.province = province;
            this.city = city;
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

        public static class Builder {
            private String openid;
            private String unionid;
            private String nickname;
            private String headImgUrl;
            private Integer sex;
            private String country;
            private String province;
            private String city;

            public Builder openid(String openid) {
                this.openid = openid;
                return this;
            }

            public Builder unionid(String unionid) {
                this.unionid = unionid;
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

            public WeChatAuthResult build() {
                return new WeChatAuthResult(openid, unionid, nickname, headImgUrl, sex, country, province, city);
            }
        }
    }
}
