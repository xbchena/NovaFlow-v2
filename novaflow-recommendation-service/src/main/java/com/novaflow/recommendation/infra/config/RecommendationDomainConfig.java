package com.novaflow.recommendation.infra.config;

import com.novaflow.recommendation.domain.model.recommendation.service.DeduplicationService;
import com.novaflow.recommendation.domain.model.recommendation.service.RecommendationGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecommendationDomainConfig {

    @Bean
    public RecommendationGenerator recommendationGenerator() {
        return new RecommendationGenerator.RuleBasedGenerator();
    }

    @Bean
    public DeduplicationService deduplicationService() {
        return new DeduplicationService.DefaultDeduplicationService();
    }
}
