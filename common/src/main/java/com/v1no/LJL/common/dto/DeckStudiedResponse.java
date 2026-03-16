package com.v1no.LJL.common.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record DeckStudiedResponse(
        UUID deckId,
        boolean started
) {}
