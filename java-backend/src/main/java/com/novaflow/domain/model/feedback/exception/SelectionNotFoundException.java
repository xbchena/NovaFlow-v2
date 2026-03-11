package com.novaflow.domain.model.feedback.exception;

import com.novaflow.domain.model.shared.exception.DomainErrorCode;
import com.novaflow.domain.model.shared.exception.DomainException;

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
