package com.v1no.LJL.learning_service.client.fallback;

import java.util.List;

import org.springframework.stereotype.Component;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.learning_service.client.ProgressServiceClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProgressServiceClientFallback implements ProgressServiceClient {

    @Override
    public ApiResponse<List<LessonProgressSummary>> getProgressByLessonIds(
        UUID userId,
        List<UUID> lessonIds
    ) {
        log.warn("Progress-service unavailable, fallback triggered: userId={}", userId);
        return ApiResponse.ok(List.of());
    }
}
