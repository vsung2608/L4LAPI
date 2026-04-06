package com.v1no.LJL.community_service.service.impl;

import com.v1no.LJL.common.dto.CursorPage;
import com.v1no.LJL.common.exception.DuplicateResourceException;
import com.v1no.LJL.community_service.mapper.ChatGroupMapper;
import com.v1no.LJL.community_service.model.dto.request.CreateChatGroupRequest;
import com.v1no.LJL.community_service.model.dto.request.UpdateChatGroupRequest;
import com.v1no.LJL.community_service.model.dto.response.ChatGroupResponse;
import com.v1no.LJL.community_service.model.dto.response.ChatGroupSummaryResponse;
import com.v1no.LJL.community_service.model.entity.ChatGroup;
import com.v1no.LJL.community_service.repository.ChatGroupRepository;
import com.v1no.LJL.community_service.repository.GroupMessageRepository;
import com.v1no.LJL.community_service.service.ChatGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatGroupServiceImpl implements ChatGroupService {

    private final ChatGroupRepository groupRepository;
    private final GroupMessageRepository messageRepository;
    private final ChatGroupMapper mapper;

    @Override
    @Transactional
    public ChatGroupResponse create(CreateChatGroupRequest request, UUID createdBy) {
        if (groupRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Chat group name already exists: " + request.name());
        }
        ChatGroup group = ChatGroup.builder()
                .name(request.name())
                .createdBy(createdBy)
                .build();
        group = groupRepository.save(group);
        log.info("ChatGroup created: {} by user {}", group.getId(), createdBy);
        return mapper.toResponse(group);
    }

    @Override
    public ChatGroupResponse getById(UUID id) {
        return mapper.toResponse(findActiveOrThrow(id));
    }

    @Override
    public Page<ChatGroupSummaryResponse> listActive(Pageable pageable) {
        return groupRepository
                .findAllByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(mapper::toSummary);
    }

    @Override
    public CursorPage<ChatGroupSummaryResponse> listActiveByCursor(String cursor, int size) {
        Pageable limit = PageRequest.of(0, size + 1); // fetch one extra to detect hasNext
        List<ChatGroup> groups;

        if (cursor == null || cursor.isBlank()) {
            groups = groupRepository.findActiveFirstPage(limit);
        } else {
            LocalDateTime cursorTime = parseCursor(cursor);
            groups = groupRepository.findActiveByCursor(cursorTime, limit);
        }

        String nextCursor = null;
        if (groups.size() > size) {
            groups = groups.subList(0, size);
            nextCursor = groups.get(groups.size() - 1).getCreatedAt().toString();
        }

        List<ChatGroupSummaryResponse> content = groups.stream()
                .map(mapper::toSummary)
                .toList();

        return CursorPage.of(content, nextCursor, size);
    }

    @Override
    @Transactional
    public ChatGroupResponse update(UUID id, UpdateChatGroupRequest request, UUID requesterId) {
        ChatGroup group = findActiveOrThrow(id);
        assertIsCreator(group, requesterId);

        if (!group.getName().equals(request.name())
                && groupRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Chat group name already exists: " + request.name());
        }
        group.setName(request.name());
        log.info("ChatGroup {} updated by user {}", id, requesterId);
        return mapper.toResponse(groupRepository.save(group));
    }

    @Override
    @Transactional
    public void deactivate(UUID id, UUID requesterId) {
        ChatGroup group = findActiveOrThrow(id);
        assertIsCreator(group, requesterId);

        group.setIsActive(false);
        groupRepository.save(group);

        int deleted = messageRepository.softDeleteAllByGroupId(id, LocalDateTime.now());
        log.info("ChatGroup {} deactivated by user {}; {} messages soft-deleted", id, requesterId, deleted);
    }

    private ChatGroup findActiveOrThrow(UUID id) {
        return groupRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new DuplicateResourceException("Chat group not found: " + id));
    }

    private void assertIsCreator(ChatGroup group, UUID requesterId) {
        if (!group.getCreatedBy().equals(requesterId)) {
            throw new DuplicateResourceException("You are not the creator of this chat group: " + group.getId());
        }
    }

    private LocalDateTime parseCursor(String cursor) {
        try {
            return LocalDateTime.parse(cursor);
        } catch (DateTimeParseException e) {
            throw new DuplicateResourceException("Invalid cursor format");
        }
    }
}
