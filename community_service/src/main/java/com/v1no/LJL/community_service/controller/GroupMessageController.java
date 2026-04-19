package com.v1no.LJL.community_service.controller;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.common.dto.CursorPage;
import com.v1no.LJL.community_service.model.dto.request.EditMessageRequest;
import com.v1no.LJL.community_service.model.dto.request.SendMessageRequest;
import com.v1no.LJL.community_service.model.dto.response.GroupMessageResponse;
import com.v1no.LJL.community_service.service.GroupMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/messages")
@RequiredArgsConstructor
@Validated
public class GroupMessageController {

    private final GroupMessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String USER_ID_HEADER    = "X-User-Id";
    private static final String MODERATOR_HEADER  = "X-Is-Moderator";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<GroupMessageResponse> send(
            @PathVariable UUID groupId,
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @Valid @RequestBody SendMessageRequest request) {

        GroupMessageResponse response = messageService.send(groupId, request, userId);
        messagingTemplate.convertAndSend("/topic/groups/" + groupId + "/messages", response);
        return ApiResponse.ok(response);
    }

    @GetMapping
    public ApiResponse<Page<GroupMessageResponse>> listOffset(
            @PathVariable UUID groupId,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ApiResponse.ok(messageService.listByGroup(groupId, pageable));
    }

    @GetMapping("/cursor")
    public ApiResponse<CursorPage<GroupMessageResponse>> listCursor(
            @PathVariable UUID groupId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "50") @Min(1) @Max(200) int size) {
        return ApiResponse.ok(messageService.listByGroupByCursor(groupId, cursor, size));
    }

    @PutMapping("/{messageId}")
    public ApiResponse<GroupMessageResponse> edit(
            @PathVariable UUID groupId,
            @PathVariable UUID messageId,
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @Valid @RequestBody EditMessageRequest request) {
        return ApiResponse.ok(messageService.edit(groupId, messageId, request, userId));
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID groupId,
            @PathVariable UUID messageId,
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @RequestHeader(value = MODERATOR_HEADER, defaultValue = "false") boolean isModerator) {
        messageService.delete(groupId, messageId, userId, isModerator);
    }
}
