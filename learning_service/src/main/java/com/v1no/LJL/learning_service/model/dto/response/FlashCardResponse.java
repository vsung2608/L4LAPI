package com.v1no.LJL.learning_service.model.dto.response;

import lombok.Builder;
import java.time.Instant;

@Builder
public record FlashCardResponse(
        Long id,
        String content,
        String transcription,
        String translation,
        String exampleLang,
        String exampleViet,
        String explaintLang,
        String explaintViet,
        String hint,
        String difficulty,
        Integer displayOrder,
        Long deckId,
        Instant createdAt,
        Instant updatedAt
) {}