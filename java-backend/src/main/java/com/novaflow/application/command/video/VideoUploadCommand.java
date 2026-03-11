package com.novaflow.application.command.video;

import lombok.Builder;
import lombok.Getter;

/**
 * 视频上传命令
 */
@Getter
@Builder
public class VideoUploadCommand {
    private final Integer duration;
    private final Double latitude;
    private final Double longitude;
}
