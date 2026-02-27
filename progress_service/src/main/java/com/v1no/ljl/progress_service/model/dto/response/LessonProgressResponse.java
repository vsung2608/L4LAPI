package com.v1no.ljl.progress_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.v1no.ljl.progress_service.model.enums.LearningMode;
import com.v1no.ljl.progress_service.model.enums.LessonStatus;

public record LessonProgressResponse(
    UUID id,
    UUID userId,
    UUID lessonId,
    LearningMode mode,
    LessonStatus status,
    Integer currentSentenceIndex,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    LocalDateTime lastAccessedAt
) {}
