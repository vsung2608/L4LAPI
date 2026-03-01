package com.v1no.LJL.learning_service.model.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLessonRequest(
    @NotNull(message = "Category is required")
    UUID categoryId,

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @Size(max = 1000, message = "YouTube video URL must not exceed 1000 characters")
    String youtubeVideoUrl,

    @NotNull(message = "Level is required")
    String level,

    @NotNull(message = "Display order is required")
    @Min(value = 0, message = "Display order must be >= 0")
    Integer displayOrder
) {}