package com.v1no.LJL.learning_service.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSentenceRequest(
    @NotBlank(message = "Content is required")
    String content,

    String transcription,

    String translation,

    @Min(value = 0, message = "Start time must be >= 0")
    Integer startTimeSeconds,

    @Min(value = 0, message = "End time must be >= 0")
    Integer endTimeSeconds,

    @NotNull(message = "Order index is required")
    @Min(value = 0, message = "Order index must be >= 0")
    Integer orderIndex
) {}
