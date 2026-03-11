package com.novaflow.application.command.feedback;

import lombok.Builder;
import lombok.Getter;

/**
 * 提交反馈命令
 */
@Getter
@Builder
public class SubmitFeedbackCommand {
    private final String selectionId;
    private final String feedbackType; // positive, neutral, negative
    private final String comment;
    private final Integer rating;
}
