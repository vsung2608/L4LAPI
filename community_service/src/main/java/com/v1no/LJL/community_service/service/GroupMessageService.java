package com.v1no.LJL.community_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v1no.LJL.common.dto.CursorPage;
import com.v1no.LJL.community_service.model.dto.request.EditMessageRequest;
import com.v1no.LJL.community_service.model.dto.request.SendMessageRequest;
import com.v1no.LJL.community_service.model.dto.response.GroupMessageResponse;

import java.util.UUID;

public interface GroupMessageService {

    GroupMessageResponse send(UUID groupId, SendMessageRequest request, UUID senderId);

    GroupMessageResponse edit(UUID groupId, UUID messageId,
                              EditMessageRequest request, UUID requesterId);

    void delete(UUID groupId, UUID messageId, UUID requesterId, boolean isModerator);

    Page<GroupMessageResponse> listByGroup(UUID groupId, Pageable pageable);

    CursorPage<GroupMessageResponse> listByGroupByCursor(UUID groupId, String cursor, int size);
}
