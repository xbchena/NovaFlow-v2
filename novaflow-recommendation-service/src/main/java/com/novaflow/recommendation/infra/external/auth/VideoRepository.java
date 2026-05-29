package com.novaflow.recommendation.infra.external.auth;

import com.novaflow.common.domain.valueobject.VideoId;

import java.util.Optional;

/**
 * 视频仓储接口（本地存根）
 * 来自 video 服务的跨服务依赖
 */
public interface VideoRepository {

    /**
     * 根据ID查找视频
     */
    Optional<Video> findById(VideoId videoId);
}
