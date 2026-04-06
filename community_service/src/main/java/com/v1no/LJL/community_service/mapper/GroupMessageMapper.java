package com.v1no.LJL.community_service.mapper;

import com.v1no.LJL.community_service.model.dto.response.GroupMessageResponse;
import com.v1no.LJL.community_service.model.entity.GroupMessage;
import org.springframework.stereotype.Component;

@Component
public class GroupMessageMapper {

    public GroupMessageResponse toResponse(GroupMessage message) {
        return new GroupMessageResponse(
                message.getId(),
                message.getGroup().getId(),
                message.getUserId(),
                message.getUsername(),
                message.getAvatarUrl(),
                message.getMessageType(),
                message.getContent(),
                message.getIsDeleted(),
                message.getDeletedAt(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
