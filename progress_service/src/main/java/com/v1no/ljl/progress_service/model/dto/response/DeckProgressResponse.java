package com.v1no.ljl.progress_service.model.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record DeckProgressResponse(
        boolean started,
        Long lastStudiedCardId,
        List<CardProgressResponse> records
) {}
