package com.novaflow.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 视频列表响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoListResponse {

    private List<VideoInfo> videos;
    private Integer total;

    /**
     * 构造函数
     */
    public VideoListResponse(List<VideoInfo> videos, int total) {
        this.videos = videos;
        this.total = total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoInfo {
        private String id;
        private String ossUrl;
        private String thumbnailUrl;
        private Integer duration;
        private String status;
        private String createdAt;
    }
}
