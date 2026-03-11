package com.novaflow.application.assembler;

import com.novaflow.domain.model.video.Video;
import com.novaflow.infrastructure.external.ai.VideoAnalysisService.AnalysisResult;
import com.novaflow.interfaces.dto.response.VideoAnalysisResponse;
import com.novaflow.interfaces.dto.response.VideoListResponse;

import java.util.List;

/**
 * 视频聚合转换器
 * 负责领域对象与DTO之间的转换
 */
public class VideoAssembler {

    /**
     * 转换为视频列表响应
     */
    public static VideoListResponse toVideoListResponse(List<Video> videos, long total) {
        List<VideoListResponse.VideoInfo> videoInfos = videos.stream()
                .map(VideoAssembler::toVideoInfo)
                .toList();

        return new VideoListResponse(videoInfos, (int) total);
    }

    /**
     * 转换为视频信息
     */
    public static VideoListResponse.VideoInfo toVideoInfo(Video video) {
        return new VideoListResponse.VideoInfo(
                video.getVideoId().getValue(),
                video.getStorageInfo() != null ? video.getStorageInfo().getOssUrl() : null,
                video.getMetadata() != null ? video.getMetadata().getThumbnailUrl() : null,
                video.getMetadata() != null ? video.getMetadata().getDuration() : null,
                video.getStatus().getValue(),
                video.getCreatedAt().toString()
        );
    }

    /**
     * 转换为分析响应
     */
    public static VideoAnalysisResponse toAnalysisResponse(Video video, AnalysisResult analysisResult) {
        return new VideoAnalysisResponse(
                true,
                analysisResult.getSceneType(),
                analysisResult.getSceneDescription(),
                analysisResult.getRecommendations(),
                analysisResult.getNearbyPlaces()
        );
    }
}
