package com.v1no.LJL.community_service.repository;

import com.v1no.LJL.community_service.model.entity.Comment;
import com.v1no.LJL.community_service.model.enums.CommentTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Page<Comment> findByTargetTypeAndTargetIdAndParentIdIsNullAndIsDeletedFalse(
            CommentTargetType targetType,
            UUID targetId,
            Pageable pageable
    );

    List<Comment> findByParentIdAndIsDeletedFalseOrderByCreatedAtAsc(UUID parentId);

    Page<Comment> findByTargetTypeAndTargetId(
            CommentTargetType targetType,
            UUID targetId,
            Pageable pageable
    );

    Optional<Comment> findByIdAndIsDeletedFalse(UUID id);

    @Modifying
    @Query("UPDATE Comment c SET c.isDeleted = true, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id = :id")
    void softDeleteById(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Comment c SET c.isDeleted = true, c.updatedAt = CURRENT_TIMESTAMP WHERE c.parentId = :parentId")
    void softDeleteByParentId(@Param("parentId") UUID parentId);

    @Modifying
    @Query("UPDATE Comment c SET c.replyCount = c.replyCount + 1 WHERE c.id = :id")
    void incrementReplyCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Comment c SET c.replyCount = c.replyCount - 1 WHERE c.id = :id AND c.replyCount > 0")
    void decrementReplyCount(@Param("id") UUID id);

    Long countByTargetTypeAndTargetIdAndIsDeletedFalse(CommentTargetType targetType, UUID targetId);

    Page<Comment> findByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);
}