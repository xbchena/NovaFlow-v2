package com.novaflow.domain.repository;

import com.novaflow.domain.model.video.Video;
import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.model.shared.valueobject.VideoId;

import java.util.List;
import java.util.Optional;

/**
 * 视频仓储接口
 * 定义视频聚合的持久化操作
 */
public interface VideoRepository extends Repository<Video, VideoId> {

    /**
     * 根据用户ID查找视频列表
     */
    List<Video> findByUserId(UserId userId);

    /**
     * 根据用户ID分页查找视频列表
     */
    List<Video> findByUserId(UserId userId, int page, int pageSize);

    /**
     * 统计用户的视频数量
     */
    long countByUserId(UserId userId);

    /**
     * 根据状态查找视频列表
     */
    List<Video> findByStatus(com.novaflow.domain.model.video.valueobject.VideoStatus status);

    /**
     * 查找需要处理的视频（状态为PROCESSING）
     */
    List<Video> findPendingProcessingVideos(int limit);
}
