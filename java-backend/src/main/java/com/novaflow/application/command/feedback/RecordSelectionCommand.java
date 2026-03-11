package com.novaflow.application.command.feedback;

import lombok.Builder;
import lombok.Getter;

/**
 * 记录选择命令
 */
@Getter
@Builder
public class RecordSelectionCommand {
    private final String recommendationId;
    private final String selectedFood;
    private final String selectedPlace;
    private final String placeName;
    private final String feedbackType; // positive, neutral, negative
    private final String feedbackComment;
    private final Integer rating;
}
