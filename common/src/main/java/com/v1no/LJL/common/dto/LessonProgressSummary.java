package com.v1no.LJL.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LessonProgressSummary(
    UUID lessonId,
    String status,
    Integer currentSentenceIndex,
    LocalDateTime lastAccessedAt
) {}