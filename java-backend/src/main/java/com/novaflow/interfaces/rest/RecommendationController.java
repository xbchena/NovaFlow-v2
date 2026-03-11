package com.novaflow.interfaces.rest;

import com.novaflow.application.service.RecommendationApplicationService;
import com.novaflow.interfaces.dto.response.Result;
import com.novaflow.interfaces.dto.request.SelectionRequest;
import com.novaflow.interfaces.dto.response.RecommendationDetailResponse;
import com.novaflow.interfaces.dto.response.RecommendationHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 推荐控制器
 * 处理推荐相关的HTTP请求
 */
@Tag(name = "推荐管理", description = "推荐相关接口")
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationApplicationService recommendationApplicationService;

    /**
     * 生成推荐
     */
    @Operation(summary = "生成推荐", description = "基于视频分析生成食物推荐")
    @PostMapping("/generate")
    public Result<RecommendationDetailResponse> generateRecommendation(
            @RequestParam String videoId,
            @RequestHeader("Authorization") String token) {
        RecommendationDetailResponse response = recommendationApplicationService.generateRecommendation(videoId, token);
        return Result.ok(response);
    }

    /**
     * 获取推荐历史
     */
    @Operation(summary = "获取推荐历史", description = "获取用户的推荐历史记录")
    @GetMapping("/history")
    public Result<RecommendationHistoryResponse> getHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        RecommendationHistoryResponse response = recommendationApplicationService.getHistory(token, page, pageSize);
        return Result.ok(response);
    }

    /**
     * 记录用户选择
     */
    @Operation(summary = "记录用户选择", description = "记录用户选择的推荐和反馈")
    @PostMapping("/select")
    public Result<Void> recordSelection(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SelectionRequest request) {
        recommendationApplicationService.recordSelection(token, request);
        return Result.ok();
    }

    /**
     * 获取推荐详情
     */
    @Operation(summary = "获取推荐详情", description = "获取推荐结果的详细信息")
    @GetMapping("/{id}")
    public Result<RecommendationDetailResponse> getDetail(@PathVariable String id) {
        RecommendationDetailResponse response = recommendationApplicationService.getDetail(id);
        return Result.ok(response);
    }
}
