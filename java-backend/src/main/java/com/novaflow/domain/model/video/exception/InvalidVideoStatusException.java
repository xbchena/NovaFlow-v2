package com.novaflow.domain.model.video.exception;

import com.novaflow.domain.model.shared.exception.DomainErrorCode;
import com.novaflow.domain.model.shared.exception.DomainException;

/**
 * 无效的视频状态异常
 * 当尝试进行不允许的状态转换时抛出
 */
public class InvalidVideoStatusException extends DomainException {

    public InvalidVideoStatusException(String message) {
        super(DomainErrorCode.INVALID_VIDEO_STATUS.getCode(), message);
    }

    public InvalidVideoStatusException(String currentStatus, String targetStatus) {
        super(DomainErrorCode.INVALID_VIDEO_STATUS.getCode(),
                String.format("无法从状态 %s 转换到 %s", currentStatus, targetStatus));
    }

    public InvalidVideoStatusException() {
        super(DomainErrorCode.INVALID_VIDEO_STATUS.getCode(), DomainErrorCode.INVALID_VIDEO_STATUS.getMessage());
    }
}
