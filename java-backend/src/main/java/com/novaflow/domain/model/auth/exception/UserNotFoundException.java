package com.novaflow.domain.model.auth.exception;

import com.novaflow.domain.model.shared.exception.DomainErrorCode;
import com.novaflow.domain.model.shared.exception.DomainException;

/**
 * 用户不存在异常
 */
public class UserNotFoundException extends DomainException {

    public UserNotFoundException(String message) {
        super(DomainErrorCode.USER_NOT_FOUND.getCode(), message);
    }

    public UserNotFoundException() {
        super(DomainErrorCode.USER_NOT_FOUND.getCode(), DomainErrorCode.USER_NOT_FOUND.getMessage());
    }
}
