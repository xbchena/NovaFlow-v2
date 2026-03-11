package com.novaflow.domain.model.recommendation.exception;

import com.novaflow.domain.model.shared.exception.DomainErrorCode;
import com.novaflow.domain.model.shared.exception.DomainException;

/**
 * 推荐生成失败异常
 */
public class RecommendationGenerationFailedException extends DomainException {

    public RecommendationGenerationFailedException(String message) {
        super(DomainErrorCode.RECOMMENDATION_GENERATION_FAILED.getCode(), message);
    }

    public RecommendationGenerationFailedException(String message, Throwable cause) {
        super(DomainErrorCode.RECOMMENDATION_GENERATION_FAILED.getCode(), message, cause);
    }

    public RecommendationGenerationFailedException() {
        super(DomainErrorCode.RECOMMENDATION_GENERATION_FAILED.getCode(), DomainErrorCode.RECOMMENDATION_GENERATION_FAILED.getMessage());
    }
}
