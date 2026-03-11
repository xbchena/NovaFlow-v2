package com.novaflow.infrastructure.persistence.mapper;

import com.novaflow.infrastructure.persistence.po.RecommendationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推荐Mapper接口
 * 使用XML配置SQL
 */
@Mapper
public interface RecommendationMapper {

    /**
     * 根据ID查询推荐
     */
    RecommendationPO selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询推荐列表
     */
    List<RecommendationPO> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查询推荐列表
     */
    List<RecommendationPO> selectByUserIdAndPage(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    /**
     * 根据视频ID查询推荐
     */
    RecommendationPO selectByVideoId(@Param("videoId") Long videoId);

    /**
     * 查询用户的最新推荐
     */
    RecommendationPO selectLatestByUserId(@Param("userId") Long userId);

    /**
     * 插入推荐
     */
    int insert(RecommendationPO recommendationPO);

    /**
     * 更新推荐
     */
    int update(RecommendationPO recommendationPO);

    /**
     * 更新推荐内容
     */
    int updateContent(@Param("id") Long id, @Param("content") String content);

    /**
     * 根据ID删除推荐（软删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 统计用户的推荐数量
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 批量查询推荐
     */
    List<RecommendationPO> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 统计推荐总数
     */
    long countTotal();

    /**
     * 根据场景类型查询推荐
     */
    List<RecommendationPO> selectBySceneType(@Param("sceneType") String sceneType);
}
