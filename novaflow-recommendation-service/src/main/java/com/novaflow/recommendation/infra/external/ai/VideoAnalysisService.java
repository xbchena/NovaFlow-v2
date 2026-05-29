package com.novaflow.recommendation.infra.external.ai;

import com.novaflow.recommendation.interfaces.dto.response.VideoAnalysisResponse.FoodRecommendation;
import com.novaflow.recommendation.interfaces.dto.response.VideoAnalysisResponse.NearbyPlace;

import java.util.List;

/**
 * 视频分析服务接口
 * 处理视频AI分析相关功能
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
        // 添加 getter 方法以兼容 Lombok 风格的调用
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

        // 添加兼容方法
        public List<String> detectedObjects() {
            return List.of(); // 默认返回空列表
        }

        public List<String> foodItems() {
            return List.of(); // 默认返回空列表
        }
    }
}
