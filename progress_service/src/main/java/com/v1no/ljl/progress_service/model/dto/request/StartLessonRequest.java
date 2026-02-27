package com.v1no.ljl.progress_service.model.dto.request;

import java.util.UUID;

import com.v1no.ljl.progress_service.model.enums.LearningMode;

import jakarta.validation.constraints.NotNull;

public record StartLessonRequest(
    @NotNull(message = "User ID is required")
    UUID userId,

    @NotNull(message = "Lesson ID is required")
    UUID lessonId,

    @NotNull(message = "Learning mode is required")
    LearningMode mode  
) {}
