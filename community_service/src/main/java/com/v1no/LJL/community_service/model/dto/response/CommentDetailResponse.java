package com.v1no.LJL.community_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.v1no.LJL.community_service.model.enums.CommentTargetType;

import lombok.Builder;

@Builder
public record CommentDetailResponse(
        UUID id,
        UUID userId,
        CommentTargetType targetType,
        UUID targetId,
        UUID parentId,
        String content,
        Integer replyCount,
        Boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CommentDetailResponse> replies
) {
    
}
