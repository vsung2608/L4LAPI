package com.v1no.LJL.community_service.mapper;
import com.v1no.LJL.community_service.model.dto.request.CommentCreateRequest;
import com.v1no.LJL.community_service.model.dto.request.CommentUpdateRequest;
import com.v1no.LJL.community_service.model.dto.response.CommentDetailResponse;
import com.v1no.LJL.community_service.model.dto.response.CommentSummaryResponse;
import com.v1no.LJL.community_service.model.entity.Comment;
import org.springframework.stereotype.Component;
 
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
 
@Component
public class CommentMapper {
 
    public CommentDetailResponse toDetail(Comment comment) {
        if (comment == null) return null;
 
        List<CommentDetailResponse> replies = comment.getReplies() == null
                ? Collections.emptyList()
                : comment.getReplies().stream()
                        .map(this::toDetail)
                        .collect(Collectors.toList());
 
        return CommentDetailResponse.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .targetType(comment.getTargetType())
                .targetId(comment.getTargetId())
                .parentId(comment.getParentId())
                .content(Boolean.TRUE.equals(comment.getIsDeleted()) ? "[Bình luận đã bị xóa]" : comment.getContent())
                .replyCount(comment.getReplyCount())
                .isDeleted(comment.getIsDeleted())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(replies)
                .build();
    }
 
    public CommentSummaryResponse toSummary(Comment comment) {
        if (comment == null) return null;
 
        return CommentSummaryResponse.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .targetType(comment.getTargetType())
                .targetId(comment.getTargetId())
                .parentId(comment.getParentId())
                .content(Boolean.TRUE.equals(comment.getIsDeleted()) ? "[Bình luận đã bị xóa]" : comment.getContent())
                .replyCount(comment.getReplyCount())
                .createdAt(comment.getCreatedAt())
                .build();
    }
 
    public List<CommentSummaryResponse> toSummaryList(List<Comment> comments) {
        if (comments == null) return Collections.emptyList();
        return comments.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }
 
    public Comment toEntity(CommentCreateRequest request, UUID userId) {
        return Comment.builder()
                .userId(userId)
                .targetType(request.targetType())
                .targetId(request.targetId())
                .parentId(request.parentId())
                .content(request.content())
                .replyCount(0)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
 
    public void updateEntity(Comment comment, CommentUpdateRequest request) {
        comment.setContent(request.content());
        comment.setUpdatedAt(LocalDateTime.now());
    }
}
