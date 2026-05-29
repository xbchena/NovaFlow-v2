package com.novaflow.recommendation.infra.external.auth;

import com.novaflow.recommendation.domain.model.recommendation.valueobject.Location;

/**
 * 视频聚合根（本地存根）
 * 来自 video 服务的跨服务依赖，仅包含推荐服务所需字段
 */
public class Video {

    private final String videoId;
    private final String userId;
    private final Location location;

    public Video(String videoId, String userId, Location location) {
        this.videoId = videoId;
        this.userId = userId;
        this.location = location;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getUserId() {
        return userId;
    }

    public Location getLocation() {
        return location != null ? location : Location.empty();
    }
}
