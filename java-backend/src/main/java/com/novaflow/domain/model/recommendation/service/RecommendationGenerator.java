package com.novaflow.domain.model.recommendation.service;

import com.novaflow.domain.model.recommendation.valueobject.RecommendationContext;
import com.novaflow.domain.model.recommendation.valueobject.RecommendationItem;
import com.novaflow.domain.model.recommendation.valueobject.SceneAnalysis;
import com.novaflow.domain.model.recommendation.exception.RecommendationGenerationFailedException;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐生成领域服务
 * 负责基于场景分析生成食物推荐
 */
public interface RecommendationGenerator {

    /**
     * 生成推荐
     * @param sceneAnalysis 场景分析结果
     * @param context 推荐上下文
     * @return 推荐项列表
     * @throws RecommendationGenerationFailedException 如果生成失败
     */
    List<RecommendationItem> generate(SceneAnalysis sceneAnalysis, RecommendationContext context)
            throws RecommendationGenerationFailedException;

    /**
     * 默认实现 - 基于规则的推荐生成器
     * 可以被AI生成器替换
     */
    class RuleBasedGenerator implements RecommendationGenerator {

        @Override
        public List<RecommendationItem> generate(SceneAnalysis sceneAnalysis, RecommendationContext context) {
            List<RecommendationItem> items = new ArrayList<>();

            // 基于场景类型生成推荐
            if (sceneAnalysis.isRestaurantScene()) {
                // 餐厅场景推荐
                items.add(RecommendationItem.of("特色炒菜", "中餐", "根据餐厅场景推荐", "约30元", 85));
                items.add(RecommendationItem.of("清汤面", "面食", "清淡易消化", "约15元", 75));
                items.add(RecommendationItem.of("红烧肉", "中餐", "经典菜品", "约45元", 80));
            } else if (sceneAnalysis.isStreetScene()) {
                // 街边场景推荐
                items.add(RecommendationItem.of("煎饼果子", "小吃", "街边经典", "约8元", 90));
                items.add(RecommendationItem.of("烤串", "烧烤", "夜宵首选", "约20元", 88));
                items.add(RecommendationItem.of("凉皮", "小吃", "清爽解腻", "约10元", 82));
            } else if (sceneAnalysis.isHomeScene()) {
                // 家庭场景推荐
                items.add(RecommendationItem.of("家常菜", "中餐", "温馨家常", "约25元", 78));
                items.add(RecommendationItem.of("汤品", "汤类", "营养健康", "约15元", 72));
            }

            // 基于时间段调整推荐
            if (context.isBreakfastTime()) {
                items.add(0, RecommendationItem.of("豆浆油条", "早餐", "经典搭配", "约10元", 95));
                items.add(1, RecommendationItem.of("包子", "早餐", "营养便捷", "约8元", 90));
            } else if (context.isLunchTime()) {
                items.add(0, RecommendationItem.of("盖浇饭", "快餐", "工作日首选", "约20元", 88));
            } else if (context.isDinnerTime()) {
                items.add(0, RecommendationItem.of("火锅", "中餐", "聚餐首选", "约80元", 85));
            } else if (context.isLateNightSnackTime()) {
                items.add(0, RecommendationItem.of("烧烤", "烧烤", "夜宵必备", "约40元", 92));
                items.add(1, RecommendationItem.of("小龙虾", "海鲜", "夜宵热门", "约100元", 90));
            }

            // 基于已识别的食物生成相关推荐
            for (String foodItem : sceneAnalysis.getFoodItems()) {
                items.add(RecommendationItem.of(
                        foodItem + "（类似）",
                        "根据场景识别",
                        "基于视频识别的食物",
                        "价格待定",
                        70
                ));
            }

            if (items.isEmpty()) {
                throw new RecommendationGenerationFailedException("无法生成推荐：场景信息不足");
            }

            return items;
        }
    }
}
