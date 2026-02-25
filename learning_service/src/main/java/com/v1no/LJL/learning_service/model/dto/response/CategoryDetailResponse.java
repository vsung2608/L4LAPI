package com.v1no.LJL.learning_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;

public record CategoryDetailResponse(
    UUID id,
    String name,
    String description,
    String thumbnailUrl,
    Integer displayOrder,
    ContentStatus status,
    List<LessonSummaryResponse> lessons,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
