package com.v1no.LJL.community_service.controller;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.common.dto.CursorPage;
import com.v1no.LJL.community_service.model.dto.request.CreateChatGroupRequest;
import com.v1no.LJL.community_service.model.dto.request.UpdateChatGroupRequest;
import com.v1no.LJL.community_service.model.dto.response.ChatGroupResponse;
import com.v1no.LJL.community_service.model.dto.response.ChatGroupSummaryResponse;
import com.v1no.LJL.community_service.service.ChatGroupService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Validated
public class ChatGroupController {

    private final ChatGroupService groupService;
    private static final String USER_ID_HEADER = "X-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChatGroupResponse> create(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @Valid @RequestBody CreateChatGroupRequest request) {
        return ApiResponse.ok(groupService.create(request, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ChatGroupResponse> getById(@PathVariable UUID id) {
        return ApiResponse.ok(groupService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<ChatGroupSummaryResponse>> listOffset(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ApiResponse.ok(groupService.listActive(pageable));
    }

    @GetMapping("/cursor")
    public ApiResponse<CursorPage<ChatGroupSummaryResponse>> listCursor(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.ok(groupService.listActiveByCursor(cursor, size));
    }

    @PutMapping("/{id}")
    public ApiResponse<ChatGroupResponse> update(
            @PathVariable UUID id,
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @Valid @RequestBody UpdateChatGroupRequest request) {
        return ApiResponse.ok(groupService.update(id, request, userId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @PathVariable UUID id,
            @RequestHeader(USER_ID_HEADER) UUID userId) {
        groupService.deactivate(id, userId);
    }
}
