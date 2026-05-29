package com.novaflow.feedback.interfaces.rest;

import com.novaflow.common.dto.Result;
import com.novaflow.feedback.application.command.RecordSelectionCommand;
import com.novaflow.feedback.application.service.UserFeedbackApplicationService;
import com.novaflow.feedback.interfaces.dto.request.SelectionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "反馈管理", description = "用户反馈相关接口")
@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final UserFeedbackApplicationService feedbackApplicationService;

    @Operation(summary = "记录用户选择")
    @PostMapping("/selection")
    public Result<Void> recordSelection(
            @Valid @RequestBody SelectionRequest request,
            @RequestHeader("Authorization") String token) {
        RecordSelectionCommand command = RecordSelectionCommand.builder()
                .recommendationId(request.getRecommendationId())
                .selectedFood(request.getSelectedFood())
                .selectedPlace(request.getSelectedPlace())
                .feedbackType(request.getFeedback())
                .build();
        feedbackApplicationService.recordSelection(command, token);
        return Result.ok();
    }
}
