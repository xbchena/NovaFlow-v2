package com.novaflow.infrastructure.persistence.mapper;

import com.novaflow.infrastructure.persistence.po.UserSelectionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户选择记录Mapper接口
 * 使用XML配置SQL
 */
@Mapper
public interface UserSelectionMapper {

    /**
     * 根据ID查询选择记录
     */
    UserSelectionPO selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询选择记录列表
     */
    List<UserSelectionPO> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和视频ID查询选择记录
     */
    UserSelectionPO selectByUserIdAndVideoId(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId
    );

    /**
     * 根据推荐ID查询选择记录列表
     */
    List<UserSelectionPO> selectByRecommendationId(@Param("recommendationId") Long recommendationId);

    /**
     * 插入选择记录
     */
    int insert(UserSelectionPO userSelectionPO);

    /**
     * 更新选择记录
     */
    int update(UserSelectionPO userSelectionPO);

    /**
     * 根据ID删除选择记录（软删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 统计用户的选择记录数量
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 查询用户最近的选择记录
     */
    List<UserSelectionPO> selectRecentByUserId(
            @Param("userId") Long userId,
            @Param("limit") int limit
    );

    /**
     * 统计用户接受推荐的次数
     */
    long countAcceptedByUserId(@Param("userId") Long userId);

    /**
     * 统计用户拒绝推荐的次数
     */
    long countRejectedByUserId(@Param("userId") Long userId);

    /**
     * 批量查询选择记录
     */
    List<UserSelectionPO> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 统计选择记录总数
     */
    long countTotal();
}
