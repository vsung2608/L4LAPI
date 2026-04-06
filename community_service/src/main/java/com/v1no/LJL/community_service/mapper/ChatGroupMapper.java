package com.v1no.LJL.community_service.mapper;

import com.v1no.LJL.community_service.model.dto.response.ChatGroupResponse;
import com.v1no.LJL.community_service.model.dto.response.ChatGroupSummaryResponse;
import com.v1no.LJL.community_service.model.entity.ChatGroup;
import org.springframework.stereotype.Component;

@Component
public class ChatGroupMapper {

    public ChatGroupResponse toResponse(ChatGroup group) {
        return new ChatGroupResponse(
                group.getId(),
                group.getName(),
                group.getCreatedBy(),
                group.getMessageCount(),
                group.getIsActive(),
                group.getCreatedAt(),
                group.getUpdatedAt()
        );
    }

    public ChatGroupSummaryResponse toSummary(ChatGroup group) {
        return new ChatGroupSummaryResponse(
                group.getId(),
                group.getName(),
                group.getMessageCount(),
                group.getIsActive(),
                group.getCreatedAt()
        );
    }
}
