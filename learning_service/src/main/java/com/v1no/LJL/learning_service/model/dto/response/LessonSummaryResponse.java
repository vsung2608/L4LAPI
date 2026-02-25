package com.v1no.LJL.learning_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.model.enums.JlptLevel;

public record LessonSummaryResponse(
    UUID id,
    UUID categoryId,
    String categoryName,
    String title,
    String description,
    String thumbnailUrl,
    String youtubeVideoId,
    Integer durationSeconds,
    JlptLevel level,
    Integer displayOrder,
    ContentStatus status,
    int totalSentences,
    LocalDateTime createdAt
) {}
