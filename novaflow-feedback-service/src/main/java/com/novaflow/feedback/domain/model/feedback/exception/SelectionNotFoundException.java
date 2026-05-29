package com.novaflow.feedback.domain.model.feedback.exception;

import com.novaflow.common.domain.exception.DomainErrorCode;
import com.novaflow.common.domain.exception.DomainException;

/**
 * 选择记录不存在异常
 */
public class SelectionNotFoundException extends DomainException {

    public SelectionNotFoundException(String message) {
        super(DomainErrorCode.SELECTION_NOT_FOUND.getCode(), message);
    }

    public SelectionNotFoundException() {
        super(DomainErrorCode.SELECTION_NOT_FOUND.getCode(), DomainErrorCode.SELECTION_NOT_FOUND.getMessage());
    }
}
