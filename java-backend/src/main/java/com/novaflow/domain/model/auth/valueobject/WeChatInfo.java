package com.novaflow.domain.model.auth.valueobject;

import com.novaflow.domain.model.shared.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 微信用户信息值对象
 * 存储从微信获取的用户基本信息
 */
@Getter
@EqualsAndHashCode
public class WeChatInfo implements ValueObject {

    private final String nickname;
    private final String avatar;
    private final Integer gender;
    private final String country;
    private final String province;
    private final String city;

    // Explicit getters for Lombok compatibility
    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public Integer getGender() {
        return gender;
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

    private WeChatInfo(Builder builder) {
        this.nickname = builder.nickname;
        this.avatar = builder.avatar;
        this.gender = builder.gender;
        this.country = builder.country;
        this.province = builder.province;
        this.city = builder.city;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nickname;
        private String avatar;
        private Integer gender;
        private String country;
        private String province;
        private String city;

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder gender(Integer gender) {
            this.gender = gender;
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

        public WeChatInfo build() {
            return new WeChatInfo(this);
        }
    }

    /**
     * 创建默认的微信信息
     */
    public static WeChatInfo createDefault() {
        return builder()
                .nickname("微信用户")
                .avatar("")
                .gender(0)
                .build();
    }

    /**
     * 验证是否有效
     */
    public boolean isValid() {
        return nickname != null && !nickname.trim().isEmpty();
    }
}
