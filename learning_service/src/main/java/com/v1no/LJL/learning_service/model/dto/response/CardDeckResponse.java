package com.v1no.LJL.learning_service.model.dto.response;

import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CardDeckResponse(
        UUID id,
        String title,
        String thumbnailUrl,
        String description,
        LanguageCode language,
        boolean started,
        int totalCards,
        Instant createdAt,
        Instant updatedAt
) {}