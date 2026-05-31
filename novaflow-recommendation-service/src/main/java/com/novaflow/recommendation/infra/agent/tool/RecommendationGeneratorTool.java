package com.novaflow.recommendation.infra.agent.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaflow.recommendation.domain.model.recommendation.service.RecommendationGenerator;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.RecommendationContext;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.RecommendationItem;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.SceneAnalysis;
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
 * 推荐生成工具
 *
 * <p>调用 RecommendationGenerator 根据场景分析结果和时间段生成个性化美食推荐。
 * 将场景信息转换为领域值对象后委托给推荐引擎。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationGeneratorTool {

    private final RecommendationGenerator recommendationGenerator;
    private final ObjectMapper objectMapper;

    @Tool(description = "根据视频场景分析结果生成个性化美食推荐。"
            + "结合场景类型、识别到的食物和当前时间段，生成推荐列表。"
            + "在视频分析完成后调用此工具获取推荐。")
    public String generateRecommendations(
            @ToolParam(description = "场景类型，如 '餐厅'、'街边'、'家庭' 等") String sceneType,
            @ToolParam(description = "场景描述，如 '一家热闹的川菜餐厅，桌上摆满了火锅和串串'") String sceneDescription,
            @ToolParam(description = "识别到的食物列表，JSON 数组字符串，如 '[\"火锅\",\"串串\",\"毛肚\"]'",
                       required = false) String detectedFoods,
            @ToolParam(description = "当前时段：早餐、午餐、晚餐、夜宵。默认根据当前时间自动判断",
                       required = false) String timeOfDay) {

        log.info("生成推荐: sceneType={}, timeOfDay={}", sceneType, timeOfDay);

        try {
            // 解析检测到的食物
            List<String> foodItems = List.of();
            if (detectedFoods != null && !detectedFoods.isBlank()) {
                try {
                    foodItems = objectMapper.readValue(detectedFoods, new TypeReference<List<String>>() {});
                } catch (JsonProcessingException e) {
                    log.warn("解析食物列表失败，使用空列表: {}", detectedFoods);
                }
            }

            // 自动推断时段
            String effectiveTimeOfDay = (timeOfDay != null && !timeOfDay.isBlank()) ? timeOfDay : inferTimeOfDay();

            // 构建场景分析
            SceneAnalysis sceneAnalysis = SceneAnalysis.of(sceneType, sceneDescription, foodItems, foodItems);

            // 构建推荐上下文
            RecommendationContext context = RecommendationContext.empty();

            // 生成推荐
            List<RecommendationItem> items = recommendationGenerator.generate(sceneAnalysis, context);

            // 构建返回 JSON
            var recList = items.stream()
                    .map(item -> {
                        Map<String, Object> rec = new LinkedHashMap<>();
                        rec.put("foodName", item.getFoodName());
                        rec.put("foodType", item.getFoodType());
                        rec.put("reason", item.getReason() != null ? item.getReason() : "");
                        rec.put("priceHint", item.getPriceHint() != null ? item.getPriceHint() : "");
                        rec.put("matchScore", item.getMatchScore());
                        return rec;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("sceneType", sceneType);
            response.put("timeOfDay", effectiveTimeOfDay);
            response.put("total", recList.size());
            response.put("recommendations", recList);

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            log.error("生成推荐失败: sceneType={}", sceneType, e);
            try {
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("success", false);
                error.put("sceneType", sceneType);
                error.put("error", "生成推荐失败: " + e.getMessage());
                return objectMapper.writeValueAsString(error);
            } catch (JsonProcessingException ex) {
                return "{\"success\":false,\"error\":\"生成推荐失败\"}";
            }
        }
    }

    /**
     * 根据当前时间推断时段
     */
    private String inferTimeOfDay() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour >= 5 && hour < 9) return "早餐";
        if (hour >= 9 && hour < 11) return "上午茶";
        if (hour >= 11 && hour < 14) return "午餐";
        if (hour >= 14 && hour < 17) return "下午茶";
        if (hour >= 17 && hour < 21) return "晚餐";
        return "夜宵";
    }
}
