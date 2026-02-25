package com.v1no.LJL.learning_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;

public record CategorySummaryResponse(
    UUID id,
    String name,
    String description,
    String thumbnailUrl,
    Integer displayOrder,
    ContentStatus status,
    LocalDateTime createdAt
) {}
