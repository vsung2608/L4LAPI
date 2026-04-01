package com.v1no.LJL.community_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.v1no.LJL.community_service.model.enums.CommentTargetType;

import lombok.Builder;

@Builder
public record RatingDetailResponse(
        UUID id,
        UUID userId,
        CommentTargetType targetType,
        Integer score,
        String review,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}