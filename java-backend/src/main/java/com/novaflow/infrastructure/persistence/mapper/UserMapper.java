package com.novaflow.infrastructure.persistence.mapper;

import com.novaflow.infrastructure.persistence.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 使用XML配置SQL
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    UserPO selectById(@Param("id") Long id);

    /**
     * 根据OpenID查询用户
     */
    UserPO selectByOpenID(@Param("openid") String openid);

    /**
     * 根据手机号查询用户
     */
    UserPO selectByPhone(@Param("phone") String phone);

    /**
     * 插入用户
     */
    int insert(UserPO userPO);

    /**
     * 更新用户
     */
    int update(UserPO userPO);

    /**
     * 根据ID删除用户（软删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 检查OpenID是否存在
     */
    int countByOpenID(@Param("openid") String openid);

    /**
     * 检查手机号是否存在
     */
    int countByPhone(@Param("phone") String phone);

    /**
     * 检查手机号是否被其他用户使用
     */
    int countByPhoneAndIdNot(@Param("phone") String phone, @Param("id") Long id);

    /**
     * 批量查询用户
     */
    List<UserPO> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 查询所有用户（分页）
     */
    List<UserPO> selectByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计用户总数
     */
    long countTotal();
}
