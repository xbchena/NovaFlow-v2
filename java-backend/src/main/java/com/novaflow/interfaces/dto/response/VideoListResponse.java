package com.novaflow.interfaces.dto.response;

import java.util.List;

/**
 * 视频列表响应DTO
 */
public class VideoListResponse {

    private List<VideoInfo> videos;
    private Integer total;

    // 无参构造函数
    public VideoListResponse() {
    }

    // 全参构造函数
    public VideoListResponse(List<VideoInfo> videos, int total) {
        this.videos = videos;
        this.total = total;
    }

    // Getters
    public List<VideoInfo> getVideos() {
        return videos;
    }

    public Integer getTotal() {
        return total;
    }

    // Setters
    public void setVideos(List<VideoInfo> videos) {
        this.videos = videos;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 视频信息
     */
    public static class VideoInfo {
        private String id;
        private String ossUrl;
        private String thumbnailUrl;
        private Integer duration;
        private String status;
        private String createdAt;

        // 无参构造函数
        public VideoInfo() {
        }

        // 全参构造函数
        public VideoInfo(String id, String ossUrl, String thumbnailUrl,
                        Integer duration, String status, String createdAt) {
            this.id = id;
            this.ossUrl = ossUrl;
            this.thumbnailUrl = thumbnailUrl;
            this.duration = duration;
            this.status = status;
            this.createdAt = createdAt;
        }

        // Getters
        public String getId() {
            return id;
        }

        public String getOssUrl() {
            return ossUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public Integer getDuration() {
            return duration;
        }

        public String getStatus() {
            return status;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        // Setters
        public void setId(String id) {
            this.id = id;
        }

        public void setOssUrl(String ossUrl) {
            this.ossUrl = ossUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}
