package com.v1no.ljl.progress_service.model.dto.request;

import com.v1no.ljl.progress_service.model.enums.LessonStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProgressRequest(
    @NotNull(message = "Sentence index is required")
    @Min(value = 0, message = "Sentence index must be >= 0")
    Integer currentSentenceIndex,

    @NotNull(message = "Status is required")
    LessonStatus status
) {}
