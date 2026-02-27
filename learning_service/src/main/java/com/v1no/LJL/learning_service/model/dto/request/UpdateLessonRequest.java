package com.v1no.LJL.learning_service.model.dto.request;

import java.util.UUID;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.model.enums.JlptLevel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateLessonRequest(
    @NotNull(message = "Category is required")
    UUID categoryId,

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    String thumbnailUrl,

    @Size(max = 50, message = "YouTube video ID must not exceed 50 characters")
    String youtubeVideoId,

    @Min(value = 0, message = "Duration must be >= 0")
    Integer durationSeconds,

    @NotNull(message = "Level is required")
    String level,

    @NotNull(message = "Display order is required")
    @Min(value = 0, message = "Display order must be >= 0")
    Integer displayOrder,

    @NotNull(message = "Status is required")
    ContentStatus status
) {}
