package com.novaflow.recommendation.infra.agent.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaflow.recommendation.domain.model.user.UserProfile;
import com.novaflow.recommendation.infra.memory.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户历史查询工具
 *
 * <p>查询用户的饮食偏好、过敏原、推荐历史等信息（L3 长期记忆），
 * 帮助 Agent 生成个性化推荐。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserHistoryQueryTool {

    private final UserProfileService userProfileService;
    private final ObjectMapper objectMapper;

    @Tool(description = "查询用户的历史偏好和推荐记录，包括饮食偏好、过敏原、喜欢的菜系、价格偏好等。"
            + "用于在推荐前了解用户画像，生成个性化推荐。")
    public String queryUserHistory(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "查询类型：preferences（仅偏好）、clicks（点击统计）、recommendations（推荐数量）、all（全部信息）。默认 all",
                       required = false) String type) {

        log.info("查询用户历史: userId={}, type={}", userId, type);

        try {
            var profileOpt = userProfileService.findByUserId(userId);

            if (profileOpt.isEmpty()) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("userId", userId);
                response.put("isNewUser", true);
                response.put("message", "该用户尚无历史记录，可以主动询问用户的饮食偏好");
                return objectMapper.writeValueAsString(response);
            }

            UserProfile profile = profileOpt.get();
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("userId", userId);
            response.put("isNewUser", profile.isNewUser());

            // 根据 type 决定返回内容
            String queryType = (type != null && !type.isBlank()) ? type.toLowerCase() : "all";

            if ("all".equals(queryType) || "preferences".equals(queryType)) {
                response.put("dietPreferences", profile.getDietPreferences());
                response.put("allergies", profile.getAllergies());
                response.put("preferredCuisines", profile.getPreferredCuisines());
                response.put("preferredPriceRange", profile.getPreferredPriceRange());
            }

            if ("all".equals(queryType) || "clicks".equals(queryType)) {
                response.put("totalClicks", profile.getTotalClicks());
                response.put("totalRecommendations", profile.getTotalRecommendations());
            }

            if ("all".equals(queryType) || "recommendations".equals(queryType)) {
                response.put("totalRecommendations", profile.getTotalRecommendations());
            }

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            log.error("查询用户历史失败: userId={}", userId, e);
            try {
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("success", false);
                error.put("userId", userId);
                error.put("error", "查询用户历史失败: " + e.getMessage());
                return objectMapper.writeValueAsString(error);
            } catch (JsonProcessingException ex) {
                return "{\"success\":false,\"error\":\"查询用户历史失败\"}";
            }
        }
    }
}
