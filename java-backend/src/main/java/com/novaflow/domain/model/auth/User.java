package com.novaflow.domain.model.auth;

import com.novaflow.domain.event.DomainEvent;
import com.novaflow.domain.model.auth.exception.*;
import com.novaflow.domain.model.auth.valueobject.*;
import com.novaflow.domain.model.auth.event.*;
import com.novaflow.domain.model.shared.aggregate.AggregateRoot;
import com.novaflow.domain.model.shared.valueobject.UserId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户聚合根
 * 管理用户的认证、个人信息和偏好设置
 */
@Getter
public class User extends AggregateRoot {

    private final UserId userId;
    private OpenID openid;
    private UnionID unionid;
    private String nickname;
    private String avatar;
    private PhoneNumber phone;
    private UserPreferences preferences;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

    // 私有构造函数，通过工厂方法创建
    private User(UserId userId, OpenID openid, UnionID unionid, WeChatInfo weChatInfo, UserPreferences preferences) {
        this.userId = userId;
        this.openid = openid;
        this.unionid = unionid;
        this.nickname = weChatInfo.getNickname();
        this.avatar = weChatInfo.getAvatar();
        this.phone = null;
        this.preferences = preferences;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleted = false;
    }

    /**
     * 通过微信创建新用户（工厂方法）
     */
    public static User createViaWeChat(OpenID openid, UnionID unionid, WeChatInfo weChatInfo) {
        if (openid == null || !openid.isValid()) {
            throw new IllegalArgumentException("OpenID不能为空");
        }

        UserId userId = UserId.generate();
        UserPreferences preferences = UserPreferences.empty();

        User user = new User(userId, openid, unionid, weChatInfo, preferences);
        user.addDomainEvent(new UserCreatedEvent(userId.getValue(), openid.getValue()));

        return user;
    }

    /**
     * 通过手机号创建新用户（工厂方法）
     */
    public static User createViaPhone(PhoneNumber phone, String nickname) {
        if (phone == null) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        UserId userId = UserId.generate();
        UserPreferences preferences = UserPreferences.empty();

        User user = new User(userId, null, null,
                WeChatInfo.builder().nickname(nickname).build(), preferences);
        user.phone = phone;
        user.addDomainEvent(new UserCreatedEvent(userId.getValue(), phone.getValue()));

        return user;
    }

    /**
     * 绑定微信账号
     */
    public void bindWeChat(OpenID openid, UnionID unionid) {
        if (this.openid != null && this.openid.isValid()) {
            throw new UserAlreadyExistsException("用户已绑定微信账号");
        }

        this.openid = openid;
        this.unionid = unionid;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new WeChatBoundEvent(this.userId.getValue(), openid.getValue()));
    }

    /**
     * 绑定手机号
     */
    public void bindPhone(PhoneNumber phone) {
        if (this.phone != null) {
            throw new UserAlreadyExistsException("用户已绑定手机号");
        }

        this.phone = phone;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new PhoneBoundEvent(this.userId.getValue(), phone.getValue()));
    }

    /**
     * 更换手机号
     */
    public void changePhone(PhoneNumber newPhone) {
        if (newPhone == null) {
            throw new IllegalArgumentException("新手机号不能为空");
        }

        PhoneNumber oldPhone = this.phone;
        this.phone = newPhone;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new PhoneChangedEvent(this.userId.getValue(),
                oldPhone != null ? oldPhone.getValue() : null,
                newPhone.getValue()));
    }

    /**
     * 更新用户信息
     */
    public void updateProfile(String nickname, String avatar) {
        boolean changed = false;

        if (nickname != null && !nickname.trim().isEmpty() && !nickname.equals(this.nickname)) {
            this.nickname = nickname;
            changed = true;
        }

        if (avatar != null && !avatar.trim().isEmpty() && !avatar.equals(this.avatar)) {
            this.avatar = avatar;
            changed = true;
        }

        if (changed) {
            this.updatedAt = LocalDateTime.now();
            addDomainEvent(new UserProfileUpdatedEvent(this.userId.getValue()));
        }
    }

    /**
     * 更新偏好设置
     */
    public void updatePreferences(UserPreferences newPreferences) {
        if (newPreferences == null) {
            throw new IllegalArgumentException("偏好设置不能为空");
        }

        this.preferences = newPreferences;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new UserPreferencesUpdatedEvent(this.userId.getValue()));
    }

    /**
     * 验证微信OpenID是否匹配
     */
    public boolean verifyWeChatOpenID(OpenID openid) {
        return this.openid != null && this.openid.equals(openid);
    }

    /**
     * 验证手机号是否匹配
     */
    public boolean verifyPhone(PhoneNumber phone) {
        return this.phone != null && this.phone.equals(phone);
    }

    /**
     * 检查是否绑定了微信
     */
    public boolean hasWeChatBound() {
        return openid != null && openid.isValid();
    }

    /**
     * 检查是否绑定了手机号
     */
    public boolean hasPhoneBound() {
        return phone != null;
    }

    /**
     * 检查是否已删除
     */
    public boolean isDeleted() {
        return deleted != null && deleted;
    }

    /**
     * 软删除用户
     */
    public void delete() {
        if (this.deleted) {
            throw new UnsupportedOperationException("用户已被删除");
        }
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(new UserDeletedEvent(this.userId.getValue()));
    }

    /**
     * 激活已删除的用户
     */
    public void activate() {
        if (!this.deleted) {
            throw new UnsupportedOperationException("用户未被删除");
        }
        this.deleted = false;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String getId() {
        return userId.getValue();
    }

    /**
     * 从现有实体重建User聚合根（用于从数据库恢复）
     */
    public static User reconstruct(UserId userId, OpenID openid, UnionID unionid,
                                   String nickname, String avatar, PhoneNumber phone,
                                   UserPreferences preferences, LocalDateTime createdAt,
                                   LocalDateTime updatedAt, Boolean deleted) {
        User user = new User(userId, openid, unionid,
                WeChatInfo.builder().nickname(nickname).avatar(avatar).build(), preferences);
        user.phone = phone;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.deleted = deleted;
        return user;
    }
}
