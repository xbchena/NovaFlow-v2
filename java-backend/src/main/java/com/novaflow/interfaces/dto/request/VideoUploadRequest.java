package com.novaflow.interfaces.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 视频上传请求DTO
 */
public class VideoUploadRequest {

    /**
     * 视频时长（秒）
     */
    @NotNull(message = "视频时长不能为空")
    @Min(value = 1, message = "视频时长必须大于0")
    private Integer duration;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    // Getters
    public Integer getDuration() {
        return duration;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    // Setters
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
