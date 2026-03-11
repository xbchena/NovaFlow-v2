package com.novaflow.domain.model.video.valueobject;

import com.novaflow.domain.model.shared.valueobject.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * OSS存储信息值对象
 * 包含阿里云OSS的存储相关信息
 */
@Getter
@EqualsAndHashCode
public class OSSStorageInfo implements ValueObject {

    private final String bucket;
    private final String objectKey;
    private final String ossUrl;
    private final String region;

    private OSSStorageInfo(String bucket, String objectKey, String ossUrl, String region) {
        if (objectKey == null || objectKey.trim().isEmpty()) {
            throw new IllegalArgumentException("OSS对象Key不能为空");
        }
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.ossUrl = ossUrl;
        this.region = region;
    }

    /**
     * 创建OSS存储信息
     */
    public static OSSStorageInfo of(String bucket, String objectKey, String ossUrl, String region) {
        return new OSSStorageInfo(bucket, objectKey, ossUrl, region);
    }

    /**
     * 创建最小信息的OSS存储信息
     */
    public static OSSStorageInfo of(String objectKey, String ossUrl) {
        return new OSSStorageInfo(null, objectKey, ossUrl, null);
    }

    /**
     * 检查是否有完整URL
     */
    public boolean hasUrl() {
        return ossUrl != null && !ossUrl.trim().isEmpty();
    }

    /**
     * 检查是否有Bucket信息
     */
    public boolean hasBucket() {
        return bucket != null && !bucket.trim().isEmpty();
    }
}
