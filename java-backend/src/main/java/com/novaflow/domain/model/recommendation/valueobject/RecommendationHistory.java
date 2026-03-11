package com.novaflow.domain.model.recommendation.valueobject;

import com.novaflow.domain.model.shared.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 推荐历史值对象
 * 用于去重和个性化推荐
 */
@Getter
@EqualsAndHashCode
public class RecommendationHistory implements ValueObject {

    private final Set<String> recentlyRecommended; // 最近推荐的食物
    private final Set<String> recentlySelected;    // 最近选择的食物
    private final Set<String> recentlyRejected;    // 最近拒绝的食物
    private final LocalDateTime lastUpdated;

    private RecommendationHistory(Set<String> recentlyRecommended, Set<String> recentlySelected,
                                  Set<String> recentlyRejected, LocalDateTime lastUpdated) {
        this.recentlyRecommended = recentlyRecommended == null ? new HashSet<>() : new HashSet<>(recentlyRecommended);
        this.recentlySelected = recentlySelected == null ? new HashSet<>() : new HashSet<>(recentlySelected);
        this.recentlyRejected = recentlyRejected == null ? new HashSet<>() : new HashSet<>(recentlyRejected);
        this.lastUpdated = lastUpdated != null ? lastUpdated : LocalDateTime.now();
    }

    /**
     * 创建空的推荐历史
     */
    public static RecommendationHistory empty() {
        return new RecommendationHistory(Set.of(), Set.of(), Set.of(), LocalDateTime.now());
    }

    /**
     * 从现有数据创建推荐历史
     */
    public static RecommendationHistory of(Set<String> recentlyRecommended, Set<String> recentlySelected,
                                         Set<String> recentlyRejected) {
        return new RecommendationHistory(recentlyRecommended, recentlySelected, recentlyRejected, LocalDateTime.now());
    }

    /**
     * 添加推荐项
     */
    public RecommendationHistory addRecommended(String foodName) {
        Set<String> newRecommended = new HashSet<>(this.recentlyRecommended);
        newRecommended.add(foodName);
        return new RecommendationHistory(newRecommended, this.recentlySelected, this.recentlyRejected, LocalDateTime.now());
    }

    /**
     * 添加选择项
     */
    public RecommendationHistory addSelected(String foodName) {
        Set<String> newSelected = new HashSet<>(this.recentlySelected);
        newSelected.add(foodName);
        return new RecommendationHistory(this.recentlyRecommended, newSelected, this.recentlyRejected, LocalDateTime.now());
    }

    /**
     * 添加拒绝项
     */
    public RecommendationHistory addRejected(String foodName) {
        Set<String> newRejected = new HashSet<>(this.recentlyRejected);
        newRejected.add(foodName);
        return new RecommendationHistory(this.recentlyRecommended, this.recentlySelected, newRejected, LocalDateTime.now());
    }

    /**
     * 检查食物是否最近被推荐过
     */
    public boolean wasRecommended(String foodName) {
        return recentlyRecommended.contains(foodName);
    }

    /**
     * 检查食物是否最近被选择过
     */
    public boolean wasSelected(String foodName) {
        return recentlySelected.contains(foodName);
    }

    /**
     * 检查食物是否最近被拒绝过
     */
    public boolean wasRejected(String foodName) {
        return recentlyRejected.contains(foodName);
    }

    /**
     * 检查是否应该推荐（未被拒绝且最近未推荐）
     */
    public boolean shouldRecommend(String foodName) {
        return !wasRejected(foodName) && !wasRecommended(foodName);
    }

    /**
     * 获取推荐优先级分数
     * 基于历史数据计算
     */
    public int getPriorityScore(String foodName) {
        int score = 0;
        if (wasSelected(foodName)) {
            score += 10; // 用户喜欢
        }
        if (wasRejected(foodName)) {
            score -= 20; // 用户不喜欢
        }
        if (wasRecommended(foodName)) {
            score -= 5; // 最近已推荐
        }
        return score;
    }

    /**
     * 清理旧记录（保留最近N条）
     */
    public RecommendationHistory trim(int maxRecent) {
        return new RecommendationHistory(
                trimSet(recentlyRecommended, maxRecent),
                trimSet(recentlySelected, maxRecent),
                trimSet(recentlyRejected, maxRecent),
                LocalDateTime.now()
        );
    }

    private Set<String> trimSet(Set<String> set, int maxSize) {
        if (set.size() <= maxSize) {
            return set;
        }
        // 简单实现：保留前N个元素
        return new HashSet<>(set.stream().limit(maxSize).toList());
    }

    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        return recentlyRecommended.isEmpty() && recentlySelected.isEmpty() && recentlyRejected.isEmpty();
    }
}
