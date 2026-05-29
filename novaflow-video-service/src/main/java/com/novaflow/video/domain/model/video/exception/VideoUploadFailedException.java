package com.novaflow.video.domain.model.video.exception;

import com.novaflow.common.domain.exception.DomainErrorCode;
import com.novaflow.common.domain.exception.DomainException;

/**
 * 视频上传失败异常
 */
public class VideoUploadFailedException extends DomainException {

    public VideoUploadFailedException(String message) {
        super(DomainErrorCode.VIDEO_UPLOAD_FAILED.getCode(), message);
    }

    public VideoUploadFailedException(String message, Throwable cause) {
        super(DomainErrorCode.VIDEO_UPLOAD_FAILED.getCode(), message, cause);
    }

    public VideoUploadFailedException() {
        super(DomainErrorCode.VIDEO_UPLOAD_FAILED.getCode(), DomainErrorCode.VIDEO_UPLOAD_FAILED.getMessage());
    }
}
