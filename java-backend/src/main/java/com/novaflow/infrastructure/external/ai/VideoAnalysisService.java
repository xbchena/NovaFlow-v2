package com.novaflow.infrastructure.external.ai;

import com.novaflow.interfaces.dto.response.VideoAnalysisResponse.FoodRecommendation;
import com.novaflow.interfaces.dto.response.VideoAnalysisResponse.NearbyPlace;

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
    ) {}
}
