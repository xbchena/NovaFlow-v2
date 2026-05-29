package com.novaflow.feedback.domain.model.feedback.exception;

import com.novaflow.common.domain.exception.DomainErrorCode;
import com.novaflow.common.domain.exception.DomainException;

/**
 * 反馈已存在异常
 */
public class FeedbackAlreadyExistsException extends DomainException {

    public FeedbackAlreadyExistsException(String message) {
        super(DomainErrorCode.FEEDBACK_NOT_FOUND.getCode(), message);
    }

    public FeedbackAlreadyExistsException() {
        super(DomainErrorCode.FEEDBACK_NOT_FOUND.getCode(), "反馈已存在");
    }
}
