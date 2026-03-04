package com.v1no.LJL.learning_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;

public record LessonSummaryResponse(
    UUID id,
    String title,
    String description,
    String thumbnailUrl,
    String youtubeVideoId,
    Integer durationSeconds,
    String level,
    Integer displayOrder,
    ContentStatus status,
    int totalSentences,
    LocalDateTime createdAt
) {}
