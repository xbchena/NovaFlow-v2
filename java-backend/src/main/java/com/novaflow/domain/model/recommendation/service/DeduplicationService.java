package com.novaflow.domain.model.recommendation.service;

import com.novaflow.domain.model.recommendation.valueobject.RecommendationContext;
import com.novaflow.domain.model.recommendation.valueobject.RecommendationHistory;
import com.novaflow.domain.model.recommendation.valueobject.RecommendationItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 去重领域服务
 * 负责推荐去重和个性化排序
 */
public interface DeduplicationService {

    /**
     * 去重推荐项
     * @param items 原始推荐项
     * @param history 推荐历史
     * @param context 推荐上下文
     * @param maxItems 最大返回数量
     * @return 去重后的推荐项
     */
    List<RecommendationItem> deduplicate(List<RecommendationItem> items,
                                        RecommendationHistory history,
                                        RecommendationContext context,
                                        int maxItems);

    /**
     * 默认实现 - 基于历史的去重服务
     */
    class DefaultDeduplicationService implements DeduplicationService {

        @Override
        public List<RecommendationItem> deduplicate(List<RecommendationItem> items,
                                                   RecommendationHistory history,
                                                   RecommendationContext context,
                                                   int maxItems) {
            if (items == null || items.isEmpty()) {
                return List.of();
            }

            // 1. 过滤被拒绝的推荐
            List<RecommendationItem> filtered = items.stream()
                    .filter(item -> !history.wasRejected(item.getFoodName()))
                    .collect(Collectors.toList());

            // 2. 为每个推荐项计算综合得分
            List<ScoredItem> scoredItems = filtered.stream()
                    .map(item -> new ScoredItem(
                            item,
                            calculateScore(item, history, context)
                    ))
                    .collect(Collectors.toList());

            // 3. 按得分排序
            scoredItems.sort(Comparator.comparingInt(ScoredItem::score).reversed());

            // 4. 返回前N个
            return scoredItems.stream()
                    .limit(maxItems)
                    .map(ScoredItem::item)
                    .collect(Collectors.toList());
        }

        /**
         * 计算推荐项的综合得分
         * 基础分 + 历史调整分
         */
        private int calculateScore(RecommendationItem item, RecommendationHistory history, RecommendationContext context) {
            int baseScore = item.getMatchScore() != null ? item.getMatchScore() : 50;

            // 历史调整
            int historyAdjustment = history.getPriorityScore(item.getFoodName());

            // 时间段匹配调整
            int timeAdjustment = 0;
            if (context.isBreakfastTime() && item.getFoodType() != null && item.getFoodType().contains("早餐")) {
                timeAdjustment = 10;
            } else if (context.isLunchTime() && item.getFoodType() != null &&
                      (item.getFoodType().contains("快餐") || item.getFoodType().contains("中餐"))) {
                timeAdjustment = 10;
            } else if (context.isDinnerTime() && item.getFoodType() != null &&
                      (item.getFoodType().contains("火锅") || item.getFoodType().contains("海鲜"))) {
                timeAdjustment = 10;
            } else if (context.isLateNightSnackTime() && item.getFoodType() != null &&
                      (item.getFoodType().contains("烧烤") || item.getFoodType().contains("夜宵"))) {
                timeAdjustment = 10;
            }

            // 场景匹配调整
            int sceneAdjustment = 0;
            if (context.getUserContext("sceneType", String.class) != null) {
                String sceneType = context.getUserContext("sceneType", String.class);
                if ("餐厅".equals(sceneType) && item.getFoodType() != null &&
                    (item.getFoodType().contains("中餐") || item.getFoodType().contains("西餐"))) {
                    sceneAdjustment = 5;
                }
            }

            int finalScore = baseScore + historyAdjustment + timeAdjustment + sceneAdjustment;
            return Math.max(0, Math.min(100, finalScore)); // 限制在0-100范围
        }

        /**
         * 带分数的推荐项
         */
        private record ScoredItem(RecommendationItem item, int score) {
        }
    }
}
