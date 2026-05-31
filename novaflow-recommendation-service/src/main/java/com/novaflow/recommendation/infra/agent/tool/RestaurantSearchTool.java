package com.novaflow.recommendation.infra.agent.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaflow.recommendation.infra.external.map.MapService;
import com.novaflow.recommendation.interfaces.dto.response.RecommendationDetailResponse.NearbyPlace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 餐厅搜索工具
 *
 * <p>调用 MapService 搜索用户附近的餐厅，返回餐厅名称、地址、距离、分类等信息。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantSearchTool {

    private final MapService mapService;
    private final ObjectMapper objectMapper;

    @Tool(description = "搜索用户附近的食物相关餐厅或店铺。"
            + "根据经纬度和搜索关键词查找附近餐厅，返回名称、地址、距离等信息。"
            + "当用户需要找附近吃什么时调用此工具。")
    public String searchRestaurants(
            @ToolParam(description = "用户当前纬度") double latitude,
            @ToolParam(description = "用户当前经度") double longitude,
            @ToolParam(description = "搜索关键词，如 '火锅'、'咖啡'、'川菜' 等", required = false) String keyword,
            @ToolParam(description = "搜索半径（米），默认 2000 米", required = false) Integer radius) {

        int searchRadius = (radius != null && radius > 0) ? radius : 2000;
        log.info("搜索附近餐厅: lat={}, lon={}, keyword={}, radius={}m", latitude, longitude, keyword, searchRadius);

        try {
            List<NearbyPlace> places = mapService.searchNearbyRestaurants(latitude, longitude, searchRadius);

            // 如果有关键词，过滤结果
            if (keyword != null && !keyword.isBlank()) {
                String kw = keyword.toLowerCase();
                places = places.stream()
                        .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(kw))
                        .filter(p -> p.getCategory() != null && p.getCategory().toLowerCase().contains(kw))
                        .collect(Collectors.toList());
            }

            var restaurantList = places.stream()
                    .map(p -> {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", p.getId());
                        item.put("name", p.getName());
                        item.put("address", p.getAddress());
                        item.put("distance", p.getDistance());
                        item.put("category", p.getCategory());
                        return item;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("latitude", latitude);
            response.put("longitude", longitude);
            response.put("radius", searchRadius);
            response.put("keyword", keyword);
            response.put("total", restaurantList.size());
            response.put("restaurants", restaurantList);

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            log.error("搜索附近餐厅失败: lat={}, lon={}", latitude, longitude, e);
            try {
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("success", false);
                error.put("error", "搜索附近餐厅失败: " + e.getMessage());
                return objectMapper.writeValueAsString(error);
            } catch (JsonProcessingException ex) {
                return "{\"success\":false,\"error\":\"搜索附近餐厅失败\"}";
            }
        }
    }
}
