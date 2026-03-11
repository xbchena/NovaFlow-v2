package com.novaflow.domain.model.auth.exception;

import com.novaflow.domain.model.shared.exception.DomainErrorCode;
import com.novaflow.domain.model.shared.exception.DomainException;

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
