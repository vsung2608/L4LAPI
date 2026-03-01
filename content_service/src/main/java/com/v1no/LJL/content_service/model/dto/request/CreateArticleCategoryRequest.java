package com.v1no.LJL.content_service.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateArticleCategoryRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 100) String slug,
    @Size(max = 500) String description,
    String thumbnailUrl,
    @NotNull @Min(0) Integer displayOrder
) {}
