package com.novaflow.recommendation.domain.model.recommendation.exception;

import com.novaflow.common.domain.exception.DomainErrorCode;
import com.novaflow.common.domain.exception.DomainException;

/**
 * 场景分析失败异常
 */
public class SceneAnalysisFailedException extends DomainException {

    public SceneAnalysisFailedException(String message) {
        super(DomainErrorCode.SCENE_ANALYSIS_FAILED.getCode(), message);
    }

    public SceneAnalysisFailedException(String message, Throwable cause) {
        super(DomainErrorCode.SCENE_ANALYSIS_FAILED.getCode(), message, cause);
    }

    public SceneAnalysisFailedException() {
        super(DomainErrorCode.SCENE_ANALYSIS_FAILED.getCode(), DomainErrorCode.SCENE_ANALYSIS_FAILED.getMessage());
    }
}
