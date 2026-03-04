package com.v1no.LJL.content_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleCategoryResponse(
    UUID id,
    String name,
    String slug,
    Integer displayOrder,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
