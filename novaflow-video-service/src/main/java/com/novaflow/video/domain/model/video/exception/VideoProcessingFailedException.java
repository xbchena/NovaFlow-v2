package com.novaflow.video.domain.model.video.exception;

import com.novaflow.common.domain.exception.DomainErrorCode;
import com.novaflow.common.domain.exception.DomainException;

/**
 * 视频处理失败异常
 */
public class VideoProcessingFailedException extends DomainException {

    public VideoProcessingFailedException(String message) {
        super(DomainErrorCode.VIDEO_PROCESSING_FAILED.getCode(), message);
    }

    public VideoProcessingFailedException(String message, Throwable cause) {
        super(DomainErrorCode.VIDEO_PROCESSING_FAILED.getCode(), message, cause);
    }

    public VideoProcessingFailedException() {
        super(DomainErrorCode.VIDEO_PROCESSING_FAILED.getCode(), DomainErrorCode.VIDEO_PROCESSING_FAILED.getMessage());
    }
}
