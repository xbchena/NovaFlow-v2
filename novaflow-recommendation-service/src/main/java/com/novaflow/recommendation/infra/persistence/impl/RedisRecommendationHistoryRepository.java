package com.novaflow.recommendation.infra.persistence.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaflow.common.domain.valueobject.UserId;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.RecommendationHistory;
import com.novaflow.recommendation.domain.repository.RecommendationHistoryRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Repository
public class RedisRecommendationHistoryRepository implements RecommendationHistoryRepository {

    private static final String KEY_PREFIX = "recommendation:history:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisRecommendationHistoryRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<RecommendationHistory> findByUserId(UserId userId) {
        String json = redisTemplate.opsForValue().get(key(userId));
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        try {
            HistoryPayload payload = objectMapper.readValue(json, HistoryPayload.class);
            return Optional.of(RecommendationHistory.of(
                    payload.recentlyRecommended(),
                    payload.recentlySelected(),
                    payload.recentlyRejected()
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(UserId userId, RecommendationHistory history) {
        try {
            HistoryPayload payload = new HistoryPayload(
                    history.getRecentlyRecommended(),
                    history.getRecentlySelected(),
                    history.getRecentlyRejected()
            );
            redisTemplate.opsForValue().set(key(userId), objectMapper.writeValueAsString(payload), Duration.ofDays(30));
        } catch (Exception e) {
            throw new IllegalStateException("推荐历史保存失败", e);
        }
    }

    @Override
    public void deleteByUserId(UserId userId) {
        redisTemplate.delete(key(userId));
    }

    @Override
    public void cleanExpiredHistory(UserId userId, long expireDays) {
        redisTemplate.expire(key(userId), Duration.ofDays(expireDays));
    }

    private String key(UserId userId) {
        return KEY_PREFIX + userId.getValue();
    }

    private record HistoryPayload(
            Set<String> recentlyRecommended,
            Set<String> recentlySelected,
            Set<String> recentlyRejected
    ) {
    }
}
