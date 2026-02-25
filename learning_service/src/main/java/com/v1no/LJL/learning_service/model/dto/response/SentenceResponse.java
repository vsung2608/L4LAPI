package com.v1no.LJL.learning_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SentenceResponse(
    UUID id,
    UUID lessonId,
    String content,
    String furigana,
    String translation,
    Integer startTimeSeconds,
    Integer endTimeSeconds,
    Integer orderIndex,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
