package com.v1no.LJL.learning_service.model.dto.response;

import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record CardDeckDetailResponse(
        Long id,
        String title,
        String thumbnailUrl,
        String description,
        LanguageCode language,
        List<FlashCardResponse> cards,
        Instant createdAt,
        Instant updatedAt
) {}