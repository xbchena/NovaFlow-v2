package com.novaflow.auth.domain.model.auth.exception;

import com.novaflow.common.domain.exception.DomainErrorCode;
import com.novaflow.common.domain.exception.DomainException;

/**
 * 无效令牌异常
 */
public class InvalidTokenException extends DomainException {

    public InvalidTokenException(String message) {
        super(DomainErrorCode.TOKEN_INVALID.getCode(), message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(DomainErrorCode.TOKEN_INVALID.getCode(), message, cause);
    }

    public InvalidTokenException() {
        super(DomainErrorCode.TOKEN_INVALID.getCode(), DomainErrorCode.TOKEN_INVALID.getMessage());
    }
}
