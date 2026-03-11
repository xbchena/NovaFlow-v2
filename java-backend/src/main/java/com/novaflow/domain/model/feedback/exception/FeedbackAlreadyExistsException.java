package com.novaflow.domain.model.feedback.exception;

import com.novaflow.domain.model.shared.exception.DomainErrorCode;
import com.novaflow.domain.model.shared.exception.DomainException;

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
