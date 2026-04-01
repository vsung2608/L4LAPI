package com.v1no.LJL.community_service.service.impl;

import com.v1no.LJL.community_service.mapper.CommentMapper;
import com.v1no.LJL.community_service.model.dto.request.CommentCreateRequest;
import com.v1no.LJL.community_service.model.dto.request.CommentUpdateRequest;
import com.v1no.LJL.community_service.model.dto.response.CommentDetailResponse;
import com.v1no.LJL.community_service.model.dto.response.CommentSummaryResponse;
import com.v1no.LJL.community_service.model.entity.Comment;
import com.v1no.LJL.community_service.model.enums.CommentTargetType;
import com.v1no.LJL.community_service.repository.CommentRepository;
import com.v1no.LJL.community_service.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDetailResponse createComment(CommentCreateRequest request, UUID userId) {
        if (request.parentId() != null) {
            Comment parent = commentRepository.findByIdAndIsDeletedFalse(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parent comment not found: " + request.parentId()));

            if (parent.getParentId() != null) {
                throw new IllegalArgumentException("Nested reply is not allowed. Reply to root comment only.");
            }

            Comment saved = commentRepository.save(commentMapper.toEntity(request, userId));
            commentRepository.incrementReplyCount(request.parentId());
            log.info("Created reply [id={}] under parent [id={}] by user [id={}]",
                    saved.getId(), request.parentId(), userId);
            return commentMapper.toDetail(saved);
        }

        Comment saved = commentRepository.save(commentMapper.toEntity(request, userId));
        return commentMapper.toDetail(saved);
    }


    @Override
    @Transactional
    public CommentDetailResponse updateComment(UUID commentId, CommentUpdateRequest request, UUID userId) {
        Comment comment = getActiveCommentOrThrow(commentId);
        checkOwnership(comment, userId);

        commentMapper.updateEntity(comment, request);
        Comment updated = commentRepository.save(comment);
        log.info("Updated comment [id={}] by user [id={}]", commentId, userId);
        return commentMapper.toDetail(updated);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = getActiveCommentOrThrow(commentId);
        checkOwnership(comment, userId);

        commentRepository.softDeleteByParentId(commentId);
        commentRepository.softDeleteById(commentId);

        if (comment.getParentId() != null) {
            commentRepository.decrementReplyCount(comment.getParentId());
        }

        log.info("Soft-deleted comment [id={}] by user [id={}]", commentId, userId);
    }


    @Override
    @Transactional(readOnly = true)
    public CommentDetailResponse getCommentById(UUID commentId) {
        Comment comment = getActiveCommentOrThrow(commentId);

        List<Comment> replies = commentRepository
                .findByParentIdAndIsDeletedFalseOrderByCreatedAtAsc(commentId);
        comment.setReplies(replies);

        return commentMapper.toDetail(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentSummaryResponse> getCommentsByTarget(
            CommentTargetType targetType, UUID targetId, Pageable pageable) {
        return commentRepository
                .findByTargetTypeAndTargetIdAndParentIdIsNullAndIsDeletedFalse(targetType, targetId, pageable)
                .map(commentMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDetailResponse> getRepliesByParent(UUID parentId) {
        getActiveCommentOrThrow(parentId);
        return commentRepository
                .findByParentIdAndIsDeletedFalseOrderByCreatedAtAsc(parentId)
                .stream()
                .map(commentMapper::toDetail)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentSummaryResponse> getCommentsByUser(UUID userId, Pageable pageable) {
        return commentRepository.findByUserIdAndIsDeletedFalse(userId, pageable)
                .map(commentMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCommentsByTarget(CommentTargetType targetType, UUID targetId) {
        return commentRepository.countByTargetTypeAndTargetIdAndIsDeletedFalse(targetType, targetId);
    }


    private Comment getActiveCommentOrThrow(UUID id) {
        return commentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: " + id));
    }

    private void checkOwnership(Comment comment, UUID userId) {
        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("User [" + userId + "] is not allowed to modify comment [" + comment.getId() + "]");
        }
    }
}
