package com.novaflow.recommendation.infra.external.auth;

import com.novaflow.common.domain.valueobject.VideoId;
import com.novaflow.recommendation.domain.model.recommendation.valueobject.Location;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LocalVideoRepository implements VideoRepository {

    @Override
    public Optional<Video> findById(VideoId videoId) {
        return Optional.of(new Video(videoId.getValue(), "0", Location.empty()));
    }
}
