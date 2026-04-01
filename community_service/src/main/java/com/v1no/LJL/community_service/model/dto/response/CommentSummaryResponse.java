package com.v1no.LJL.community_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.v1no.LJL.community_service.model.enums.CommentTargetType;

import lombok.Builder;

@Builder
public record CommentSummaryResponse(
        UUID id,
        UUID userId,
        CommentTargetType targetType,
        UUID targetId,
        UUID parentId,
        String content,
        Integer replyCount,
        LocalDateTime createdAt
) { 
}
