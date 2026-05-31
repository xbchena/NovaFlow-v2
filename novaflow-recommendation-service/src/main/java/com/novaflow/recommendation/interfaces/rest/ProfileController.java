package com.novaflow.recommendation.interfaces.rest;

import com.novaflow.recommendation.domain.model.user.UserProfile;
import com.novaflow.recommendation.infra.memory.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable Long userId) {
        log.info("获取用户画像: userId={}", userId);
        return userProfileService.findByUserId(userId)
                .map(profile -> ResponseEntity.ok(toMap(profile)))
                .orElseGet(() -> {
                    UserProfile empty = UserProfile.create(userId);
                    return ResponseEntity.ok(toMap(empty));
                });
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<Map<String, Object>> updatePreferences(
            @PathVariable Long userId,
            @RequestBody Map<String, List<String>> body) {
        log.info("更新用户偏好: userId={}", userId);
        List<String> preferences = body.get("preferences");
        if (preferences == null) {
            return ResponseEntity.badRequest().build();
        }
        UserProfile profile = userProfileService.updatePreferences(userId, preferences);
        return ResponseEntity.ok(toMap(profile));
    }

    @PutMapping("/{userId}/allergies")
    public ResponseEntity<Map<String, Object>> updateAllergies(
            @PathVariable Long userId,
            @RequestBody Map<String, List<String>> body) {
        log.info("更新用户过敏信息: userId={}", userId);
        List<String> allergies = body.get("allergies");
        if (allergies == null) {
            return ResponseEntity.badRequest().build();
        }
        UserProfile profile = userProfileService.updateAllergies(userId, allergies);
        return ResponseEntity.ok(toMap(profile));
    }

    private Map<String, Object> toMap(UserProfile profile) {
        return Map.of(
                "userId", profile.getUserId(),
                "dietPreferences", profile.getDietPreferences(),
                "allergies", profile.getAllergies(),
                "preferredCuisines", profile.getPreferredCuisines(),
                "preferredPriceRange", profile.getPreferredPriceRange() != null ? profile.getPreferredPriceRange() : "",
                "totalRecommendations", profile.getTotalRecommendations(),
                "totalClicks", profile.getTotalClicks(),
                "updatedAt", profile.getUpdatedAt().toString()
        );
    }
}
