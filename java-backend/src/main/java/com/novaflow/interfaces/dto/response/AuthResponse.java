package com.novaflow.interfaces.dto.response;

import java.util.function.Consumer;

/**
 * 认证响应DTO
 */
public class AuthResponse {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 用户信息
     */
    private UserInfo user;

    private AuthResponse() {
    }

    private AuthResponse(String token, String refreshToken, UserInfo user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String refreshToken;
        private UserInfo user;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder user(UserInfo user) {
            this.user = user;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, refreshToken, user);
        }
    }

    public static class UserInfo {
        private Long id;
        private String openid;
        private String nickname;
        private String avatar;
        private String phone;

        private UserInfo() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final UserInfo userInfo = new UserInfo();

            public Builder id(Long id) {
                userInfo.id = id;
                return this;
            }

            public Builder openid(String openid) {
                userInfo.openid = openid;
                return this;
            }

            public Builder nickname(String nickname) {
                userInfo.nickname = nickname;
                return this;
            }

            public Builder avatar(String avatar) {
                userInfo.avatar = avatar;
                return this;
            }

            public Builder phone(String phone) {
                userInfo.phone = phone;
                return this;
            }

            public UserInfo build() {
                return userInfo;
            }
        }
    }
}
