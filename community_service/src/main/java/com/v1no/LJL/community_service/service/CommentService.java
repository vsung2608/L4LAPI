package com.v1no.LJL.community_service.service;

import com.v1no.LJL.community_service.model.dto.request.CommentCreateRequest;
import com.v1no.LJL.community_service.model.dto.request.CommentUpdateRequest;
import com.v1no.LJL.community_service.model.dto.response.CommentDetailResponse;
import com.v1no.LJL.community_service.model.dto.response.CommentSummaryResponse;
import com.v1no.LJL.community_service.model.enums.CommentTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CommentService {
 
    CommentDetailResponse createComment(CommentCreateRequest request, UUID userId);
 
    CommentDetailResponse updateComment(UUID commentId, CommentUpdateRequest request, UUID userId);
 
    void deleteComment(UUID commentId, UUID userId);
 
    CommentDetailResponse getCommentById(UUID commentId);
 
    Page<CommentSummaryResponse> getCommentsByTarget(
            CommentTargetType targetType,
            UUID targetId,
            Pageable pageable
    );
 
    List<CommentDetailResponse> getRepliesByParent(UUID parentId);
 
    Page<CommentSummaryResponse> getCommentsByUser(UUID userId, Pageable pageable);
 
    Long countCommentsByTarget(CommentTargetType targetType, UUID targetId);
}