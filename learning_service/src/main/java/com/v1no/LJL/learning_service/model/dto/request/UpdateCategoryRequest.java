package com.v1no.LJL.learning_service.model.dto.request;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    String thumbnailUrl,

    @NotNull(message = "Display order is required")
    @Min(value = 0, message = "Display order must be >= 0")
    Integer displayOrder,

    @NotNull(message = "Status is required")
    ContentStatus status
) {}
