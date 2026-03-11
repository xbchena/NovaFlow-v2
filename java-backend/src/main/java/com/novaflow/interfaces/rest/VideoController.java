package com.novaflow.interfaces.rest;

import com.novaflow.application.service.VideoProcessingApplicationService;
import com.novaflow.interfaces.dto.response.Result;
import com.novaflow.interfaces.dto.request.VideoUploadRequest;
import com.novaflow.interfaces.dto.response.VideoAnalysisResponse;
import com.novaflow.interfaces.dto.response.VideoListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频控制器
 * 处理视频上传和处理相关的HTTP请求
 */
@Tag(name = "视频管理", description = "视频上传和处理相关接口")
@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoProcessingApplicationService videoProcessingApplicationService;

    /**
     * 上传视频
     */
    @Operation(summary = "上传视频", description = "上传视频文件并进行分析")
    @PostMapping("/upload")
    public Result<UploadResponse> uploadVideo(
            @RequestParam("video") MultipartFile video,
            @RequestParam("duration") Integer duration,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestHeader("Authorization") String token) {

        VideoUploadRequest request = new VideoUploadRequest();
        request.setDuration(duration);
        request.setLatitude(latitude);
        request.setLongitude(longitude);

        String videoId = videoProcessingApplicationService.uploadVideo(video, request, token);

        return Result.ok(new UploadResponse(videoId, "视频上传成功，正在处理中"));
    }

    /**
     * 获取视频分析结果
     */
    @Operation(summary = "获取分析结果", description = "获取视频的AI分析结果")
    @GetMapping("/{videoId}/analysis")
    public Result<VideoAnalysisResponse> getAnalysis(@PathVariable String videoId) {
        VideoAnalysisResponse response = videoProcessingApplicationService.getAnalysisResult(videoId);
        return Result.ok(response);
    }

    /**
     * 获取用户视频列表
     */
    @Operation(summary = "获取视频列表", description = "获取用户的视频记录")
    @GetMapping
    public Result<VideoListResponse> getUserVideos(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        VideoListResponse response = videoProcessingApplicationService.getUserVideos(token, page, pageSize);
        return Result.ok(response);
    }

    /**
     * 删除视频
     */
    @Operation(summary = "删除视频", description = "删除指定的视频记录")
    @DeleteMapping("/{videoId}")
    public Result<Void> deleteVideo(
            @PathVariable String videoId,
            @RequestHeader("Authorization") String token) {
        videoProcessingApplicationService.deleteVideo(videoId, token);
        return Result.ok();
    }

    /**
     * 上传响应DTO
     */
    public record UploadResponse(
            String videoId,
            String message
    ) {}
}
