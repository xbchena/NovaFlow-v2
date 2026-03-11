package com.novaflow.domain.model.recommendation.exception;

import com.novaflow.domain.model.shared.exception.DomainErrorCode;
import com.novaflow.domain.model.shared.exception.DomainException;

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
