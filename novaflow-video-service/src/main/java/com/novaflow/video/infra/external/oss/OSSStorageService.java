package com.novaflow.video.infra.external.oss;

import com.novaflow.video.domain.model.video.valueobject.OSSStorageInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * OSS存储服务接口
 * 处理文件存储相关功能
 */
public interface OSSStorageService {

    /**
     * 上传视频
     */
    OSSStorageInfo uploadVideo(MultipartFile file, String videoId);

    /**
     * 删除视频
     */
    void deleteVideo(String objectKey);

    /**
     * 生成预签名URL
     */
    String generatePresignedUrl(String objectKey, long expirationSeconds);
}
