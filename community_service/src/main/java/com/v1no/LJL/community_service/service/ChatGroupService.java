package com.v1no.LJL.community_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v1no.LJL.common.dto.CursorPage;
import com.v1no.LJL.community_service.model.dto.request.CreateChatGroupRequest;
import com.v1no.LJL.community_service.model.dto.request.UpdateChatGroupRequest;
import com.v1no.LJL.community_service.model.dto.response.ChatGroupResponse;
import com.v1no.LJL.community_service.model.dto.response.ChatGroupSummaryResponse;

import java.util.UUID;

public interface ChatGroupService {

    ChatGroupResponse create(CreateChatGroupRequest request, UUID createdBy);

    ChatGroupResponse getById(UUID id);

    Page<ChatGroupSummaryResponse> listActive(Pageable pageable);

    CursorPage<ChatGroupSummaryResponse> listActiveByCursor(String cursor, int size);

    ChatGroupResponse update(UUID id, UpdateChatGroupRequest request, UUID requesterId);

    void deactivate(UUID id, UUID requesterId);
}
