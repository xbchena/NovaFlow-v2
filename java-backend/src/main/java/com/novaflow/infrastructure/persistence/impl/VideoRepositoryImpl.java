package com.novaflow.infrastructure.persistence.impl;

import com.novaflow.domain.model.shared.valueobject.UserId;
import com.novaflow.domain.model.shared.valueobject.VideoId;
import com.novaflow.domain.model.video.Video;
import com.novaflow.domain.model.video.valueobject.VideoStatus;
import com.novaflow.domain.model.video.valueobject.Location;
import com.novaflow.domain.model.video.valueobject.OSSStorageInfo;
import com.novaflow.domain.model.video.valueobject.VideoMetadata;
import com.novaflow.domain.repository.VideoRepository;
import com.novaflow.infrastructure.persistence.mapper.VideoMapper;
import com.novaflow.infrastructure.persistence.po.VideoPO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 视频仓储实现
 * 使用 MyBatis XML 方式操作数据库
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class VideoRepositoryImpl implements VideoRepository {

    private static final Logger log = LoggerFactory.getLogger(VideoRepositoryImpl.class);

    private final VideoMapper videoMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Video> findById(VideoId videoId) {
        VideoPO videoPO = videoMapper.selectById(Long.parseLong(videoId.getValue()));
        return Optional.ofNullable(toDomain(videoPO));
    }

    @Override
    public List<Video> findByUserId(UserId userId) {
        List<VideoPO> videoPOList = videoMapper.selectByUserId(Long.parseLong(userId.getValue()));
        return videoPOList.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Video> findByUserId(UserId userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<VideoPO> videoPOList = videoMapper.selectByUserIdAndPage(
                Long.parseLong(userId.getValue()),
                offset,
                pageSize
        );
        return videoPOList.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long countByUserId(UserId userId) {
        return videoMapper.countByUserId(Long.parseLong(userId.getValue()));
    }

    @Override
    public List<Video> findByStatus(VideoStatus status) {
        List<VideoPO> videoPOList = videoMapper.selectByStatus(status.getValue());
        return videoPOList.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Video> findPendingProcessingVideos(int limit) {
        List<VideoPO> videoPOList = videoMapper.selectPendingProcessing(limit);
        return videoPOList.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Video save(Video video) {
        VideoPO videoPO = toPO(video);

        if (videoPO.getId() == null) {
            // 新增
            videoPO.setCreatedAt(LocalDateTime.now());
            videoPO.setUpdatedAt(LocalDateTime.now());
            videoPO.setDeleted(false);
            videoMapper.insert(videoPO);
        } else {
            // 更新
            videoPO.setUpdatedAt(LocalDateTime.now());
            videoMapper.update(videoPO);
        }

        return toDomain(videoPO);
    }

    @Override
    public void deleteById(VideoId videoId) {
        videoMapper.deleteById(Long.parseLong(videoId.getValue()));
    }

    @Override
    public List<Video> findAll() {
        // 默认返回前100条，使用第一个用户ID作为示例
        // 在实际应用中，应该有一个专门的不带用户ID的查询方法
        // 这里暂时返回空列表
        return List.of();
    }

    /**
     * PO 转领域对象
     */
    private Video toDomain(VideoPO videoPO) {
        if (videoPO == null) {
            return null;
        }

        try {
            // 简化转换，创建空的值对象
            VideoMetadata metadata = VideoMetadata.of(null, null);
            Location location = Location.empty();
            OSSStorageInfo storageInfo = videoPO.getStorageInfo() != null ?
                    OSSStorageInfo.of(videoPO.getStorageInfo(), videoPO.getStorageInfo()) :
                    null;

            return Video.reconstruct(
                    VideoId.of(videoPO.getId().toString()),
                    UserId.of(videoPO.getUserId().toString()),
                    VideoStatus.of(videoPO.getStatus()),
                    storageInfo,
                    metadata,
                    location,
                    videoPO.getErrorMessage(),
                    videoPO.getCreatedAt(),
                    videoPO.getUpdatedAt(),
                    videoPO.getDeleted()
            );
        } catch (Exception e) {
            log.error("Failed to convert VideoPO to Video domain object", e);
            throw new RuntimeException("数据转换失败", e);
        }
    }

    /**
     * 领域对象转 PO
     */
    private VideoPO toPO(Video video) {
        if (video == null) {
            return null;
        }

        VideoPO videoPO = new VideoPO();

        if (video.getVideoId() != null) {
            videoPO.setId(Long.parseLong(video.getVideoId().getValue()));
        }

        if (video.getUserId() != null) {
            videoPO.setUserId(Long.parseLong(video.getUserId().getValue()));
        }

        videoPO.setStatus(video.getStatus().getValue());

        if (video.getStorageInfo() != null) {
            try {
                videoPO.setStorageInfo(objectMapper.writeValueAsString(video.getStorageInfo()));
            } catch (Exception e) {
                log.error("Failed to serialize storage info", e);
            }
        }

        if (video.getMetadata() != null) {
            try {
                videoPO.setMetadata(objectMapper.writeValueAsString(video.getMetadata()));
            } catch (Exception e) {
                log.error("Failed to serialize metadata", e);
            }
        }

        if (video.getLocation() != null) {
            try {
                videoPO.setLocation(objectMapper.writeValueAsString(video.getLocation()));
            } catch (Exception e) {
                log.error("Failed to serialize location", e);
            }
        }

        videoPO.setErrorMessage(video.getErrorMessage());
        videoPO.setCreatedAt(video.getCreatedAt());
        videoPO.setUpdatedAt(video.getUpdatedAt());
        videoPO.setDeleted(video.isDeleted());

        return videoPO;
    }
}
