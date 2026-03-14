package com.v1no.LJL.learning_service.model.dto.response;

import lombok.Builder;
import java.time.Instant;
import java.util.UUID;

@Builder
public record FlashCardResponse(
        UUID id,
        String content,
        String thumbnail,
        String transcription,
        String translation,
        String exampleLang,
        String exampleViet,
        String explaintLang,
        String explaintViet,
        String hint,
        String difficulty,
        Integer displayOrder,
        String mark,
        Integer studyCount,
        UUID deckId,
        Instant createdAt,
        Instant updatedAt
) {}