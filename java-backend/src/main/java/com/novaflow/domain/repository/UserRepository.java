package com.novaflow.domain.repository;

import com.novaflow.domain.model.auth.User;
import com.novaflow.domain.model.auth.valueobject.OpenID;
import com.novaflow.domain.model.auth.valueobject.PhoneNumber;
import com.novaflow.domain.model.shared.valueobject.UserId;

import java.util.Optional;

/**
 * 用户仓储接口
 * 定义用户聚合的持久化操作
 */
public interface UserRepository extends Repository<User, UserId> {

    /**
     * 根据OpenID查找用户
     */
    Optional<User> findByOpenID(OpenID openid);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(PhoneNumber phone);

    /**
     * 检查OpenID是否已存在
     */
    boolean existsByOpenID(OpenID openid);

    /**
     * 检查手机号是否已存在
     */
    boolean existsByPhone(PhoneNumber phone);

    /**
     * 检查手机号是否被其他用户使用
     */
    boolean existsByPhoneAndUserIdNot(PhoneNumber phone, UserId userId);
}
