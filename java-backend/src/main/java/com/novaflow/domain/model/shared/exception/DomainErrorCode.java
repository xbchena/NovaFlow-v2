package com.novaflow.domain.model.shared.exception;

/**
 * 领域异常类型枚举
 * 定义系统中常见的领域错误类型
 */
public enum DomainErrorCode {

    // 认证相关错误 (AUTH_*)
    INVALID_CREDENTIALS("AUTH_001", "无效的凭据"),
    USER_NOT_FOUND("AUTH_002", "用户不存在"),
    USER_ALREADY_EXISTS("AUTH_003", "用户已存在"),
    TOKEN_INVALID("AUTH_004", "令牌无效"),
    TOKEN_EXPIRED("AUTH_005", "令牌已过期"),

    // 视频相关错误 (VIDEO_*)
    VIDEO_NOT_FOUND("VIDEO_001", "视频不存在"),
    INVALID_VIDEO_STATUS("VIDEO_002", "无效的视频状态"),
    VIDEO_UPLOAD_FAILED("VIDEO_003", "视频上传失败"),
    VIDEO_PROCESSING_FAILED("VIDEO_004", "视频处理失败"),
    INVALID_VIDEO_FORMAT("VIDEO_005", "无效的视频格式"),

    // 推荐相关错误 (REC_*)
    RECOMMENDATION_NOT_FOUND("REC_001", "推荐不存在"),
    RECOMMENDATION_GENERATION_FAILED("REC_002", "推荐生成失败"),
    SCENE_ANALYSIS_FAILED("REC_003", "场景分析失败"),
    INVALID_RECOMMENDATION_CONTEXT("REC_004", "无效的推荐上下文"),

    // 反馈相关错误 (FEEDBACK_*)
    FEEDBACK_NOT_FOUND("FEEDBACK_001", "反馈不存在"),
    INVALID_FEEDBACK_TYPE("FEEDBACK_002", "无效的反馈类型"),
    SELECTION_NOT_FOUND("FEEDBACK_003", "选择不存在"),

    // 通用错误 (COMMON_*)
    INVALID_PARAMETER("COMMON_001", "无效的参数"),
    OPERATION_NOT_ALLOWED("COMMON_002", "不允许的操作"),
    RESOURCE_ALREADY_EXISTS("COMMON_003", "资源已存在"),
    RESOURCE_NOT_FOUND("COMMON_004", "资源不存在");

    private final String code;
    private final String message;

    DomainErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
