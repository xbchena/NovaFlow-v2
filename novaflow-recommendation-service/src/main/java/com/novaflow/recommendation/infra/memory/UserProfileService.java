package com.novaflow.recommendation.infra.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaflow.recommendation.domain.model.user.UserProfile;
import com.novaflow.recommendation.infra.persistence.mapper.UserProfileMapper;
import com.novaflow.recommendation.infra.persistence.po.UserProfilePO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final ObjectMapper objectMapper;

    public Optional<UserProfile> findByUserId(Long userId) {
        return userProfileMapper.selectByUserId(userId).map(this::toDomain);
    }

    public UserProfile getOrCreate(Long userId) {
        return findByUserId(userId).orElseGet(() -> {
            log.info("创建新用户画像: userId={}", userId);
            UserProfile profile = UserProfile.create(userId);
            save(profile);
            return profile;
        });
    }

    public UserProfile save(UserProfile profile) {
        UserProfilePO po = toPO(profile);
        userProfileMapper.upsert(po);
        log.debug("保存用户画像: userId={}", profile.getUserId());
        return profile;
    }

    public UserProfile updatePreferences(Long userId, List<String> preferences) {
        UserProfile profile = getOrCreate(userId);
        profile = profile.updatePreferences(preferences);
        return save(profile);
    }

    public UserProfile updateAllergies(Long userId, List<String> allergies) {
        UserProfile profile = getOrCreate(userId);
        profile = profile.updateAllergies(allergies);
        return save(profile);
    }

    public UserProfile incrementRecommendations(Long userId, int count) {
        UserProfile profile = getOrCreate(userId);
        profile = profile.incrementRecommendations(count);
        return save(profile);
    }

    public UserProfile incrementClicks(Long userId, int count) {
        UserProfile profile = getOrCreate(userId);
        profile = profile.incrementClicks(count);
        return save(profile);
    }

    public UserProfile updateBehaviorSummary(Long userId, Map<String, Object> summary) {
        UserProfile profile = getOrCreate(userId);
        profile = profile.updateBehaviorSummary(summary);
        return save(profile);
    }

    public String buildProfilePrompt(Long userId) {
        Optional<UserProfile> profileOpt = findByUserId(userId);
        if (profileOpt.isEmpty() || profileOpt.get().isNewUser()) {
            return "";
        }
        return profileOpt.get().toPromptText();
    }

    private UserProfile toDomain(UserProfilePO po) {
        return UserProfile.reconstruct(
                po.getUserId(),
                parseJsonList(po.getDietPreferences()),
                parseJsonList(po.getAllergies()),
                parseJsonList(po.getPreferredCuisines()),
                po.getPreferredPriceRange(),
                parseJsonMap(po.getBehaviorSummary()),
                po.getTotalRecommendations() != null ? po.getTotalRecommendations() : 0,
                po.getTotalClicks() != null ? po.getTotalClicks() : 0,
                po.getUpdatedAt(),
                po.getCreatedAt()
        );
    }

    private UserProfilePO toPO(UserProfile profile) {
        UserProfilePO po = new UserProfilePO();
        po.setUserId(profile.getUserId());
        po.setDietPreferences(writeJson(profile.getDietPreferences()));
        po.setAllergies(writeJson(profile.getAllergies()));
        po.setPreferredCuisines(writeJson(profile.getPreferredCuisines()));
        po.setPreferredPriceRange(profile.getPreferredPriceRange());
        po.setBehaviorSummary(writeJson(profile.getBehaviorSummary()));
        po.setTotalRecommendations(profile.getTotalRecommendations());
        po.setTotalClicks(profile.getTotalClicks());
        po.setUpdatedAt(LocalDateTime.now());
        po.setCreatedAt(profile.getCreatedAt());
        return po;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("解析 JSON 列表失败: {}", json);
            return List.of();
        }
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.warn("解析 JSON Map 失败: {}", json);
            return Map.of();
        }
    }

    private <T> String writeJson(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("序列化 JSON 失败: {}", value);
            return "[]";
        }
    }
}
