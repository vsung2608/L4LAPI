package com.v1no.LJL.community_service.service.impl;

import com.v1no.LJL.common.dto.CursorPage;
import com.v1no.LJL.common.exception.BusinessException;
import com.v1no.LJL.common.exception.ResourceNotFoundException;
import com.v1no.LJL.community_service.mapper.GroupMessageMapper;
import com.v1no.LJL.community_service.model.dto.request.EditMessageRequest;
import com.v1no.LJL.community_service.model.dto.request.SendMessageRequest;
import com.v1no.LJL.community_service.model.dto.response.GroupMessageResponse;
import com.v1no.LJL.community_service.model.entity.ChatGroup;
import com.v1no.LJL.community_service.model.entity.GroupMessage;
import com.v1no.LJL.community_service.model.entity.GroupMessage.MessageType;
import com.v1no.LJL.community_service.repository.ChatGroupRepository;
import com.v1no.LJL.community_service.repository.GroupMessageRepository;
import com.v1no.LJL.community_service.service.GroupMessageService;
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
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMessageServiceImpl implements GroupMessageService {

    private final GroupMessageRepository messageRepository;
    private final ChatGroupRepository groupRepository;
    private final GroupMessageMapper mapper;

    @Override
    @Transactional
    public GroupMessageResponse send(UUID groupId, SendMessageRequest request, UUID senderId) {
        ChatGroup group = findActiveGroupOrThrow(groupId);

        GroupMessage message = GroupMessage.builder()
                .group(group)
                .userId(senderId)
                .username(request.username())
                .avatarUrl(request.avatarUrl())
                .messageType(Objects.requireNonNullElse(request.messageType(), MessageType.TEXT))
                .content(request.content())
                .build();

        message = messageRepository.save(message);
        group.incrementMessageCount();
        groupRepository.save(group);

        log.info("Message {} sent to group {} by user {}", message.getId(), groupId, senderId);
        return mapper.toResponse(message);
    }

    @Override
    @Transactional
    public GroupMessageResponse edit(UUID groupId, UUID messageId,
                                     EditMessageRequest request, UUID requesterId) {
        GroupMessage message = findNonDeletedOrThrow(messageId);
        assertBelongsToGroup(message, groupId);

        if (!message.canEdit(requesterId)) {
            throw new BusinessException("You do not have permission to edit this message.");
        }
        message.setContent(request.content());
        log.info("Message {} edited by user {}", messageId, requesterId);
        return mapper.toResponse(messageRepository.save(message));
    }

    @Override
    @Transactional
    public void delete(UUID groupId, UUID messageId, UUID requesterId, boolean isModerator) {
        GroupMessage message = findNonDeletedOrThrow(messageId);
        assertBelongsToGroup(message, groupId);

        if (!message.canDelete(requesterId, isModerator)) {
            throw new BusinessException("You do not have permission to delete this message.");
        }
        message.delete();
        messageRepository.save(message);
        log.info("Message {} soft-deleted by user {} (moderator={})", messageId, requesterId, isModerator);
    }

    @Override
    public Page<GroupMessageResponse> listByGroup(UUID groupId, Pageable pageable) {
        findActiveGroupOrThrow(groupId); // validate group exists
        return messageRepository
                .findAllByGroupIdAndIsDeletedFalseOrderByCreatedAtDesc(groupId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public CursorPage<GroupMessageResponse> listByGroupByCursor(UUID groupId, String cursor, int size) {
        findActiveGroupOrThrow(groupId);
        Pageable limit = PageRequest.of(0, size + 1);

        List<GroupMessage> messages;
        if (cursor == null || cursor.isBlank()) {
            messages = messageRepository.findByGroupFirstPage(groupId, limit);
        } else {
            LocalDateTime cursorTime = parseCursor(cursor);
            messages = messageRepository.findByGroupByCursor(groupId, cursorTime, limit);
        }

        String nextCursor = null;
        if (messages.size() > size) {
            messages = messages.subList(0, size);
            nextCursor = messages.get(messages.size() - 1).getCreatedAt().toString();
        }

        List<GroupMessageResponse> content = messages.stream()
                .map(mapper::toResponse)
                .toList();

        return CursorPage.of(content, nextCursor, size);
    }

    private ChatGroup findActiveGroupOrThrow(UUID groupId) {
        return groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("The group with ID::%s does not found".formatted(groupId)));
    }

    private GroupMessage findNonDeletedOrThrow(UUID messageId) {
        return messageRepository.findByIdAndIsDeletedFalse(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("The message with ID::%s does not found".formatted(messageId)));
    }

    private void assertBelongsToGroup(GroupMessage message, UUID groupId) {
        if (!message.getGroup().getId().equals(groupId)) {
            throw new ResourceNotFoundException("The message with ID::%s does not found".formatted(message.getId()));
        }
    }

    private LocalDateTime parseCursor(String cursor) {
        try {
            return LocalDateTime.parse(cursor);
        } catch (DateTimeParseException e) {
            throw new ResourceNotFoundException("Invalid cursor format");
        }
    }
}
