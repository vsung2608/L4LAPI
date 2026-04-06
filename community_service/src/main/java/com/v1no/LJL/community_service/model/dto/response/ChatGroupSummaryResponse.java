package com.v1no.LJL.community_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatGroupSummaryResponse(
        UUID id,
        String name,
        Integer messageCount,
        Boolean isActive,
        LocalDateTime createdAt
) {}
