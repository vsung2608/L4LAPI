package com.v1no.LJL.learning_service.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
    @NotBlank String languageCode,
    @NotBlank @Size(max = 100) String name,
    @Size(max = 500) String description,
    String thumbnailUrl,
    @NotNull @Min(0) Integer displayOrder
) {}
