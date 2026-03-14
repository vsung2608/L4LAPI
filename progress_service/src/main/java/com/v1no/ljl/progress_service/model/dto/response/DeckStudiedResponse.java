package com.v1no.ljl.progress_service.model.dto.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record DeckStudiedResponse(
        UUID deckId,
        boolean started
) {}
