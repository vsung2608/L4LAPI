package com.v1no.LJL.community_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatGroupResponse(
        UUID id,
        String name,
        UUID createdBy,
        Integer messageCount,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
