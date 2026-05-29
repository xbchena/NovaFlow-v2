package com.novaflow.video.infra.external.ai;

import com.novaflow.video.interfaces.dto.response.VideoAnalysisResponse.FoodRecommendation;
import com.novaflow.video.interfaces.dto.response.VideoAnalysisResponse.NearbyPlace;

import java.util.List;

/**
 * 视频分析服务接口 (Phase 1 本地存根)
 * Phase 3 将通过 Feign 调用 recommendation-service
 */
public interface VideoAnalysisService {

    /**
     * 分析视频
     */
    AnalysisResult analyzeVideo(String videoId);

    /**
     * 获取分析结果
     */
    AnalysisResult getAnalysisResult(String videoId);

    /**
     * 分析结果
     */
    record AnalysisResult(
            String sceneType,
            String sceneDescription,
            String thumbnailUrl,
            List<FoodRecommendation> recommendations,
            List<NearbyPlace> nearbyPlaces
    ) {
        public String getSceneType() {
            return sceneType;
        }

        public String getSceneDescription() {
            return sceneDescription;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public List<FoodRecommendation> getRecommendations() {
            return recommendations;
        }

        public List<NearbyPlace> getNearbyPlaces() {
            return nearbyPlaces;
        }

        public List<String> detectedObjects() {
            return List.of();
        }

        public List<String> foodItems() {
            return List.of();
        }
    }
}
