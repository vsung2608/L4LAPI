package com.v1no.LJL.learning_service.model.dto.response;

import java.util.List;
import java.util.UUID;

public record CategoryWithLessonsResponse(
    UUID id,
    String name,
    String description,
    String thumbnailUrl,
    Integer displayOrder,
    List<LessonPreviewResponse> lessons
) {}
