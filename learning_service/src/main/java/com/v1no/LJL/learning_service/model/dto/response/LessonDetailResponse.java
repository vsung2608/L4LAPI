package com.v1no.LJL.learning_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;

public record LessonDetailResponse(
    UUID id,
    UUID categoryId,
    String categoryName,
    String title,
    String description,
    String thumbnailUrl,
    String youtubeVideoId,
    Integer durationSeconds,
    String level,
    Integer displayOrder,
    ContentStatus status,
    List<SentenceResponse> sentences,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
