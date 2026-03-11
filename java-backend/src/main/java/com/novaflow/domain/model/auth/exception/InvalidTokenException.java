package com.novaflow.domain.model.auth.exception;

import com.novaflow.domain.model.shared.exception.DomainErrorCode;
import com.novaflow.domain.model.shared.exception.DomainException;

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
