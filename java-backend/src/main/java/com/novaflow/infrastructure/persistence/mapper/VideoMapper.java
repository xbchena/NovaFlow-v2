package com.novaflow.infrastructure.persistence.mapper;

import com.novaflow.infrastructure.persistence.po.VideoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 视频Mapper接口
 * 使用XML配置SQL
 */
@Mapper
public interface VideoMapper {

    /**
     * 根据ID查询视频
     */
    VideoPO selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询视频列表
     */
    List<VideoPO> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查询视频列表
     */
    List<VideoPO> selectByUserIdAndPage(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    /**
     * 根据状态查询视频列表
     */
    List<VideoPO> selectByStatus(@Param("status") String status);

    /**
     * 查询待处理的视频（状态为PROCESSING）
     */
    List<VideoPO> selectPendingProcessing(@Param("limit") int limit);

    /**
     * 插入视频
     */
    int insert(VideoPO videoPO);

    /**
     * 更新视频
     */
    int update(VideoPO videoPO);

    /**
     * 更新视频状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新视频错误信息
     */
    int updateError(@Param("id") Long id, @Param("errorMessage") String errorMessage);

    /**
     * 更新视频元数据
     */
    int updateMetadata(@Param("id") Long id, @Param("metadata") String metadata);

    /**
     * 更新视频存储信息
     */
    int updateStorageInfo(@Param("id") Long id, @Param("storageInfo") String storageInfo);

    /**
     * 根据ID删除视频（软删除）
     */
    int deleteById(@Param("id") Long id);

    /**
     * 统计用户的视频数量
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 批量查询视频
     */
    List<VideoPO> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 统计视频总数
     */
    long countTotal();

    /**
     * 统计指定状态的视频数量
     */
    long countByStatus(@Param("status") String status);
}
