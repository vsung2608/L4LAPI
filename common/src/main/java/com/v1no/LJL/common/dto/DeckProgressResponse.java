package com.v1no.LJL.common.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record DeckProgressResponse(
        boolean started,
        Long lastStudiedCardId,
        List<CardProgressResponse> records
) {}
