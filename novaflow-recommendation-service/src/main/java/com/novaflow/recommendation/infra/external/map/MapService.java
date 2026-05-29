package com.novaflow.recommendation.infra.external.map;

import com.novaflow.recommendation.interfaces.dto.response.RecommendationDetailResponse.NearbyPlace;

import java.util.List;

/**
 * 地图服务接口
 * 处理位置和周边信息查询
 */
public interface MapService {

    /**
     * 搜索周边餐厅
     */
    List<NearbyPlace> searchNearbyRestaurants(double latitude, double longitude, int radius);

    /**
     * 获取地点详情
     */
    NearbyPlace getPlaceDetails(String placeId);

    /**
     * 计算两点间距离
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);
}
