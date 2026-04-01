package com.v1no.LJL.community_service.model.dto.request;

import com.v1no.LJL.community_service.model.enums.CommentTargetType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RatingCreateRequest(
    @NotNull(message = "Target type is required")
    CommentTargetType targetType,

    @NotNull(message = "Score is required")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 5, message = "Score must be at most 5")
    Integer score,

    @Size(max = 1000, message = "Review must not exceed 1000 characters")
    String review
) {
    
}
