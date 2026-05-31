package com.novaflow.recommendation.infra.persistence.mapper;

import com.novaflow.recommendation.infra.persistence.po.UserProfilePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 用户画像Mapper接口
 * 使用XML配置SQL
 */
@Mapper
public interface UserProfileMapper {

    /**
     * 根据用户ID查询用户画像
     */
    Optional<UserProfilePO> selectByUserId(@Param("userId") Long userId);

    /**
     * 插入用户画像
     */
    int insert(UserProfilePO profilePO);

    /**
     * 更新用户画像
     */
    int update(UserProfilePO profilePO);

    /**
     * 插入或更新用户画像
     */
    int upsert(UserProfilePO profilePO);
}
