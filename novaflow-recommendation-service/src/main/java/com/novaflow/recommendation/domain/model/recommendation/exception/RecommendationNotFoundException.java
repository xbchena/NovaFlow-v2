package com.novaflow.recommendation.domain.model.recommendation.exception;

import com.novaflow.common.domain.exception.DomainErrorCode;
import com.novaflow.common.domain.exception.DomainException;

/**
 * 推荐不存在异常
 */
public class RecommendationNotFoundException extends DomainException {

    public RecommendationNotFoundException(String message) {
        super(DomainErrorCode.RECOMMENDATION_NOT_FOUND.getCode(), message);
    }

    public RecommendationNotFoundException() {
        super(DomainErrorCode.RECOMMENDATION_NOT_FOUND.getCode(), DomainErrorCode.RECOMMENDATION_NOT_FOUND.getMessage());
    }
}
