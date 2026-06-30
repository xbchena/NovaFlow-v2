package com.novaflow.video.infra.external.ai;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalVideoAnalysisService implements VideoAnalysisService {

    @Override
    public AnalysisResult analyzeVideo(String videoId) {
        return defaultResult();
    }

    @Override
    public AnalysisResult getAnalysisResult(String videoId) {
        return defaultResult();
    }

    private AnalysisResult defaultResult() {
        return new AnalysisResult(
                "restaurant",
                "本地调试默认场景",
                null,
                List.of(),
                List.of()
        );
    }
}
