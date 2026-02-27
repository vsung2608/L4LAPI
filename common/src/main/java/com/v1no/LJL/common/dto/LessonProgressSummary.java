package com.v1no.LJL.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LessonProgressSummary(
    UUID lessonId,
    LessonProgressDetail dictation,
    LessonProgressDetail speaking
) {
    public record LessonProgressDetail(
        String status,
        Integer currentSentenceIndex,
        LocalDateTime lastAccessedAt
    ) {}
}