package com.v1no.LJL.community_service.model.dto.response;

import com.v1no.LJL.community_service.model.entity.GroupMessage.MessageType;
import java.time.LocalDateTime;
import java.util.UUID;

public record GroupMessageResponse(
        UUID id,
        UUID groupId,
        UUID userId,
        String username,
        String avatarUrl,
        MessageType messageType,
        String content,
        Boolean isDeleted,
        LocalDateTime deletedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
