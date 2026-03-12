package com.v1no.LJL.learning_service.model.dto.response;

import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CardDeckResponse(
        Long id,
        String title,
        String thumbnailUrl,
        String description,
        LanguageCode language,
        int totalCards,
        Instant createdAt,
        Instant updatedAt
) {}