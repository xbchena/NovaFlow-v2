package com.novaflow.video.domain.model.video.event;

import com.novaflow.common.domain.event.DomainEvent;
import lombok.Getter;

/**
 * 视频创建事件
 */
@Getter
public class VideoCreatedEvent extends DomainEvent {

    private final String videoId;
    private final String userId;

    public VideoCreatedEvent(String videoId, String userId) {
        super();
        this.videoId = videoId;
        this.userId = userId;
    }

    @Override
    public String getAggregateId() {
        return videoId;
    }
}
