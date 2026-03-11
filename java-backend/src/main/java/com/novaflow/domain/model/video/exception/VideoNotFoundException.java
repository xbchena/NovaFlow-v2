package com.novaflow.domain.model.video.exception;

import com.novaflow.domain.model.shared.exception.DomainErrorCode;
import com.novaflow.domain.model.shared.exception.DomainException;

/**
 * 视频不存在异常
 */
public class VideoNotFoundException extends DomainException {

    public VideoNotFoundException(String message) {
        super(DomainErrorCode.VIDEO_NOT_FOUND.getCode(), message);
    }

    public VideoNotFoundException() {
        super(DomainErrorCode.VIDEO_NOT_FOUND.getCode(), DomainErrorCode.VIDEO_NOT_FOUND.getMessage());
    }
}
