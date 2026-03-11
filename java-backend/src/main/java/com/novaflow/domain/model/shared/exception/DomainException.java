package com.novaflow.domain.model.shared.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * 领域异常基类
 * 所有领域特定异常的父类
 */
@Getter
public class DomainException extends RuntimeException {

    private final String code;
    private final UUID errorId;

    protected DomainException(String message) {
        super(message);
        this.code = "DOMAIN_ERROR";
        this.errorId = UUID.randomUUID();
    }

    protected DomainException(String code, String message) {
        super(message);
        this.code = code;
        this.errorId = UUID.randomUUID();
    }

    protected DomainException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.errorId = UUID.randomUUID();
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
        this.code = "DOMAIN_ERROR";
        this.errorId = UUID.randomUUID();
    }
}
