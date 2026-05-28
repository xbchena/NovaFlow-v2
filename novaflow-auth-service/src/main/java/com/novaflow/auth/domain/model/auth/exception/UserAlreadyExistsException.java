package com.novaflow.auth.domain.model.auth.exception;

import com.novaflow.common.domain.exception.DomainErrorCode;
import com.novaflow.common.domain.exception.DomainException;

/**
 * 用户已存在异常
 */
public class UserAlreadyExistsException extends DomainException {

    public UserAlreadyExistsException(String message) {
        super(DomainErrorCode.USER_ALREADY_EXISTS.getCode(), message);
    }

    public UserAlreadyExistsException() {
        super(DomainErrorCode.USER_ALREADY_EXISTS.getCode(), DomainErrorCode.USER_ALREADY_EXISTS.getMessage());
    }
}
