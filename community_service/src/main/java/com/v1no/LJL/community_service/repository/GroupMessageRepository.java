package com.v1no.LJL.community_service.repository;

import com.v1no.LJL.community_service.model.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, UUID> {
    Page<GroupMessage> findAllByGroupIdOrderByCreatedAtDesc(UUID groupId, Pageable pageable);

    Page<GroupMessage> findAllByGroupIdAndIsDeletedFalseOrderByCreatedAtDesc(
            UUID groupId, Pageable pageable);

    @Query("""
            SELECT m FROM GroupMessage m
            WHERE m.group.id = :groupId
              AND m.isDeleted = false
              AND m.createdAt < :cursor
            ORDER BY m.createdAt DESC
            """)
    List<GroupMessage> findByGroupByCursor(@Param("groupId") UUID groupId,
                                           @Param("cursor") LocalDateTime cursor,
                                           Pageable pageable);

    @Query("""
            SELECT m FROM GroupMessage m
            WHERE m.group.id = :groupId
              AND m.isDeleted = false
            ORDER BY m.createdAt DESC
            """)
    List<GroupMessage> findByGroupFirstPage(@Param("groupId") UUID groupId, Pageable pageable);

    Optional<GroupMessage> findByIdAndIsDeletedFalse(UUID id);

    @Modifying
    @Query("""
            UPDATE GroupMessage m
            SET m.isDeleted = true,
                m.deletedAt = :now,
                m.content   = '[Tin nhắn đã bị xóa]'
            WHERE m.group.id = :groupId
              AND m.isDeleted = false
            """)
    int softDeleteAllByGroupId(@Param("groupId") UUID groupId,
                               @Param("now") LocalDateTime now);
}
