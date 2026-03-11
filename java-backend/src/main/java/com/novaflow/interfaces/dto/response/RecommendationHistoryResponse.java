package com.novaflow.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 推荐历史响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationHistoryResponse {

    private List<RecommendationItem> recommendations;
    private Integer total;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationItem {
        private String id;
        private String sceneType;
        private String sceneDescription;
        private List<String> recommendations;
        private String createdAt;
    }
}
