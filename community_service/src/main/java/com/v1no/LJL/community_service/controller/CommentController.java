package com.v1no.LJL.community_service.controller;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.community_service.model.dto.request.CommentCreateRequest;
import com.v1no.LJL.community_service.model.dto.request.CommentUpdateRequest;
import com.v1no.LJL.community_service.model.dto.response.CommentDetailResponse;
import com.v1no.LJL.community_service.model.dto.response.CommentSummaryResponse;
import com.v1no.LJL.community_service.model.enums.CommentTargetType;
import com.v1no.LJL.community_service.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDetailResponse>> createComment(
            @Valid @RequestBody CommentCreateRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        CommentDetailResponse created = commentService.createComment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDetailResponse>> updateComment(
            @PathVariable UUID id,
            @Valid @RequestBody CommentUpdateRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        CommentDetailResponse updated = commentService.updateComment(id, request, userId);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDetailResponse>> getCommentById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(commentService.getCommentById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CommentSummaryResponse>>> getCommentsByTarget(
            @RequestParam CommentTargetType targetType,
            @RequestParam UUID targetId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CommentSummaryResponse> page =
                commentService.getCommentsByTarget(targetType, targetId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<ApiResponse<List<CommentDetailResponse>>> getReplies(
            @PathVariable UUID id
    ) {
        List<CommentDetailResponse> replies = commentService.getRepliesByParent(id);
        return ResponseEntity.ok(ApiResponse.ok(replies));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countComments(
            @RequestParam CommentTargetType targetType,
            @RequestParam UUID targetId
    ) {
        long count = commentService.countCommentsByTarget(targetType, targetId);
        return ResponseEntity.ok(ApiResponse.ok(count));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<CommentSummaryResponse>>> getMyComments(
            @RequestHeader("X-User-Id") UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CommentSummaryResponse> page = commentService.getCommentsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }
}
