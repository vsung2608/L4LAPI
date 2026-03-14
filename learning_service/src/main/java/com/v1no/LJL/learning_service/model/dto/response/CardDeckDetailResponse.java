package com.v1no.LJL.learning_service.model.dto.response;

import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record CardDeckDetailResponse(
        UUID id,
        String title,
        String thumbnailUrl,
        String description,
        LanguageCode language,
        List<FlashCardResponse> cards,
        Instant createdAt,
        Instant updatedAt
) {}