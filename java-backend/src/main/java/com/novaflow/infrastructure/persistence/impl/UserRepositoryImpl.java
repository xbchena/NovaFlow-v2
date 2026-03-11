package com.novaflow.infrastructure.persistence.impl;

import com.novaflow.domain.model.auth.User;
import com.novaflow.domain.model.auth.valueobject.OpenID;
import com.novaflow.domain.model.auth.valueobject.PhoneNumber;
import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.repository.UserRepository;
import com.novaflow.infrastructure.persistence.mapper.UserMapper;
import com.novaflow.infrastructure.persistence.po.UserPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户仓储实现
 * 使用 MyBatis XML 方式操作数据库
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(UserId userId) {
        UserPO userPO = userMapper.selectById(Long.parseLong(userId.getValue()));
        return Optional.ofNullable(toDomain(userPO));
    }

    @Override
    public Optional<User> findByOpenID(OpenID openid) {
        UserPO userPO = userMapper.selectByOpenID(openid.getValue());
        return Optional.ofNullable(toDomain(userPO));
    }

    @Override
    public Optional<User> findByPhone(PhoneNumber phone) {
        UserPO userPO = userMapper.selectByPhone(phone.getValue());
        return Optional.ofNullable(toDomain(userPO));
    }

    @Override
    public boolean existsByOpenID(OpenID openid) {
        return userMapper.countByOpenID(openid.getValue()) > 0;
    }

    @Override
    public boolean existsByPhone(PhoneNumber phone) {
        return userMapper.countByPhone(phone.getValue()) > 0;
    }

    @Override
    public boolean existsByPhoneAndUserIdNot(PhoneNumber phone, UserId userId) {
        return userMapper.countByPhoneAndIdNot(phone.getValue(), Long.parseLong(userId.getValue())) > 0;
    }

    @Override
    public User save(User user) {
        UserPO userPO = toPO(user);

        if (userPO.getId() == null) {
            // 新增
            userPO.setCreatedAt(LocalDateTime.now());
            userPO.setUpdatedAt(LocalDateTime.now());
            userPO.setDeleted(false);
            userMapper.insert(userPO);
        } else {
            // 更新
            userPO.setUpdatedAt(LocalDateTime.now());
            userMapper.update(userPO);
        }

        return toDomain(userPO);
    }

    @Override
    public void deleteById(UserId userId) {
        userMapper.deleteById(Long.parseLong(userId.getValue()));
    }

    @Override
    public List<User> findAll() {
        // 默认返回前100条
        return userMapper.selectByPage(0, 100).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * PO 转领域对象
     */
    private User toDomain(UserPO userPO) {
        if (userPO == null) {
            return null;
        }

        return User.reconstruct(
                UserId.of(userPO.getId().toString()),
                userPO.getOpenid() != null ? OpenID.of(userPO.getOpenid()) : null,
                userPO.getUnionid() != null ? com.novaflow.domain.model.auth.valueobject.UnionID.of(userPO.getUnionid()) : null,
                userPO.getNickname(),
                userPO.getAvatar(),
                userPO.getPhone() != null ? PhoneNumber.of(userPO.getPhone()) : null,
                com.novaflow.domain.model.auth.valueobject.UserPreferences.fromJson(userPO.getPreferences()),
                userPO.getCreatedAt(),
                userPO.getUpdatedAt(),
                userPO.getDeleted()
        );
    }

    /**
     * 领域对象转 PO
     */
    private UserPO toPO(User user) {
        if (user == null) {
            return null;
        }

        UserPO userPO = new UserPO();

        if (user.getId() != null) {
            userPO.setId(Long.parseLong(user.getId().getValue()));
        }

        if (user.getOpenid() != null) {
            userPO.setOpenid(user.getOpenid().getValue());
        }

        if (user.getUnionid() != null) {
            userPO.setUnionid(user.getUnionid().getValue());
        }

        userPO.setNickname(user.getNickname());
        userPO.setAvatar(user.getAvatar());

        if (user.getPhone() != null) {
            userPO.setPhone(user.getPhone().getValue());
        }

        if (user.getPreferences() != null) {
            userPO.setPreferences(user.getPreferences().toJson());
        }

        userPO.setCreatedAt(user.getCreatedAt());
        userPO.setUpdatedAt(user.getUpdatedAt());
        userPO.setDeleted(user.isDeleted());

        return userPO;
    }
}
