package com.novaflow.auth.domain.model.auth.exception;

import com.novaflow.common.domain.exception.DomainErrorCode;
import com.novaflow.common.domain.exception.DomainException;

/**
 * 无效凭据异常
 */
public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException(String message) {
        super(DomainErrorCode.INVALID_CREDENTIALS.getCode(), message);
    }

    public InvalidCredentialsException() {
        super(DomainErrorCode.INVALID_CREDENTIALS.getCode(), DomainErrorCode.INVALID_CREDENTIALS.getMessage());
    }
}
