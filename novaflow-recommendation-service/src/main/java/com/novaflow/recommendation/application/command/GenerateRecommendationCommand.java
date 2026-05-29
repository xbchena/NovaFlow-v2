package com.novaflow.recommendation.application.command;

import lombok.Builder;
import lombok.Getter;

/**
 * 生成推荐命令
 */
@Getter
@Builder
public class GenerateRecommendationCommand {
    private final String videoId;
}
