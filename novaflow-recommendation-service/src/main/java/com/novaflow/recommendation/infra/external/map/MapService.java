package com.novaflow.recommendation.infra.external.map;

import com.novaflow.recommendation.interfaces.dto.response.RecommendationDetailResponse.NearbyPlace;

import java.util.List;

/**
 * 地图服务接口
 */
public interface MapService {

    List<NearbyPlace> searchNearbyRestaurants(double latitude, double longitude, int radius);

    List<NearbyPlace> searchNearbyByCategory(
            double latitude,
            double longitude,
            int radius,
            String category
    );

    NearbyPlace getPlaceDetails(String placeId);

    double calculateDistance(double lat1, double lon1, double lat2, double lon2);
}
