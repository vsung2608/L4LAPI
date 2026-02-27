package com.v1no.LJL.learning_service.model.dto.response;

import java.util.UUID;

import com.v1no.LJL.common.dto.LessonProgressSummary;

public record LessonPreviewResponse(
    UUID id,
    String title,
    String thumbnailUrl,
    String youtubeVideoId,
    Integer durationSeconds,
    String level,
    Integer displayOrder,
    int totalSentences,
    LessonProgressSummary progress
) {}
