package com.novaflow.application.service;

import com.novaflow.application.assembler.VideoAssembler;
import com.novaflow.domain.event.DomainEvent;
import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.model.shared.valueobject.VideoId;
import com.novaflow.domain.model.video.Video;
import com.novaflow.domain.model.video.exception.InvalidVideoStatusException;
import com.novaflow.domain.model.video.exception.VideoNotFoundException;
import com.novaflow.domain.model.video.valueobject.Location;
import com.novaflow.domain.model.video.valueobject.OSSStorageInfo;
import com.novaflow.domain.model.video.valueobject.VideoMetadata;
import com.novaflow.domain.repository.DomainEventPublisher;
import com.novaflow.domain.repository.VideoRepository;
import com.novaflow.infrastructure.external.ai.VideoAnalysisService;
import com.novaflow.infrastructure.external.oss.OSSStorageService;
import com.novaflow.infrastructure.security.TokenService;
import com.novaflow.interfaces.dto.request.VideoUploadRequest;
import com.novaflow.interfaces.dto.response.VideoAnalysisResponse;
import com.novaflow.interfaces.dto.response.VideoListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频处理应用服务
 * 协调视频上传、处理和分析的用例编排
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoProcessingApplicationService {

    private final VideoRepository videoRepository;
    private final DomainEventPublisher eventPublisher;
    private final OSSStorageService ossStorageService;
    private final VideoAnalysisService videoAnalysisService;
    private final TokenService tokenService;

    /**
     * 上传视频
     */
    @Transactional
    public String uploadVideo(MultipartFile videoFile, VideoUploadRequest request, String token) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);

        // 2. 验证视频文件
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("视频文件不能为空");
        }

        // 3. 创建Video聚合
        VideoMetadata metadata = VideoMetadata.of(
                request.getDuration(),
                videoFile.getSize()
        );

        Location location = request.getLatitude() != null && request.getLongitude() != null
                ? Location.of(request.getLatitude(), request.getLongitude())
                : Location.empty();

        Video video = Video.create(userId, metadata, location);

        // 4. 保存到数据库
        video = videoRepository.save(video);

        // 5. 发布领域事件
        publishDomainEvents(video);

        // 6. 异步上传到OSS和处理
        uploadAndProcessAsync(video.getVideoId(), videoFile);

        return video.getVideoId().getValue();
    }

    /**
     * 异步上传和处理视频
     */
    private void uploadAndProcessAsync(VideoId videoId, MultipartFile videoFile) {
        // 在实际项目中，这里应该使用消息队列异步处理
        // 为了示例，使用简单的异步执行
        try {
            // 1. 上传到OSS
            OSSStorageInfo storageInfo = ossStorageService.uploadVideo(videoFile, videoId.getValue());
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new VideoNotFoundException());

            video.startUploading(storageInfo);
            video.markAsUploaded();
            video = videoRepository.save(video);
            publishDomainEvents(video);

            // 2. 分析视频
            var analysisResult = videoAnalysisService.analyzeVideo(videoId.getValue());

            // 3. 更新缩略图
            if (analysisResult.getThumbnailUrl() != null) {
                video.updateThumbnail(analysisResult.getThumbnailUrl());
            }

            // 4. 标记完成
            video.markAsCompleted();
            video = videoRepository.save(video);
            publishDomainEvents(video);

        } catch (Exception e) {
            log.error("视频处理失败: {}", videoId.getValue(), e);
            Video video = videoRepository.findById(videoId).orElse(null);
            if (video != null) {
                video.markAsFailed(e.getMessage());
                videoRepository.save(video);
                publishDomainEvents(video);
            }
        }
    }

    /**
     * 获取视频分析结果
     */
    public VideoAnalysisResponse getAnalysisResult(String videoId) {
        VideoId id = VideoId.of(videoId);
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new VideoNotFoundException());

        if (!video.getStatus().isCompleted()) {
            throw new InvalidVideoStatusException("视频尚未完成处理");
        }

        // 从分析服务获取详细结果
        var analysisResult = videoAnalysisService.getAnalysisResult(videoId);

        return VideoAssembler.toAnalysisResponse(video, analysisResult);
    }

    /**
     * 获取用户视频列表
     */
    public VideoListResponse getUserVideos(String token, int page, int pageSize) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);

        // 2. 查询视频列表
        List<Video> videos = videoRepository.findByUserId(userId, page, pageSize);
        long total = videoRepository.countByUserId(userId);

        return VideoAssembler.toVideoListResponse(videos, total);
    }

    /**
     * 删除视频
     */
    @Transactional
    public void deleteVideo(String videoId, String token) {
        // 1. 验证用户身份
        String userIdStr = tokenService.validateAccessToken(token.replace("Bearer ", ""));
        UserId userId = UserId.of(userIdStr);

        // 2. 查找视频
        VideoId id = VideoId.of(videoId);
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new VideoNotFoundException());

        // 3. 验证权限
        if (!video.belongsToUser(userId)) {
            throw new IllegalArgumentException("无权删除此视频");
        }

        // 4. 删除OSS文件
        if (video.getStorageInfo() != null) {
            ossStorageService.deleteVideo(video.getStorageInfo().getObjectKey());
        }

        // 5. 软删除视频
        video.delete();
        video = videoRepository.save(video);

        // 6. 发布事件
        publishDomainEvents(video);
    }

    /**
     * 发布领域事件
     */
    private void publishDomainEvents(Video video) {
        for (DomainEvent event : video.getDomainEvents()) {
            eventPublisher.publishAsync(event);
        }
        video.clearDomainEvents();
    }
}
