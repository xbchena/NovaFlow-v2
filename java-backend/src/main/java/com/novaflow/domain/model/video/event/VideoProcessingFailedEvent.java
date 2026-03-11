package com.novaflow.domain.model.video.event;

import com.novaflow.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 视频处理失败事件
 */
@Getter
public class VideoProcessingFailedEvent extends DomainEvent {

    private final String videoId;
    private final String userId;
    private final String errorMessage;

    public VideoProcessingFailedEvent(String videoId, String userId, String errorMessage) {
        super();
        this.videoId = videoId;
        this.userId = userId;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getAggregateId() {
        return videoId;
    }
}
