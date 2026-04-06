package com.v1no.LJL.community_service.repository;

import com.v1no.LJL.community_service.model.entity.ChatGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, UUID> {
    Page<ChatGroup> findAllByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<ChatGroup> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ChatGroup> findAllByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(
            UUID createdBy, Pageable pageable);

    @Query("""
            SELECT g FROM ChatGroup g
            WHERE g.isActive = true
              AND g.createdAt < :cursor
            ORDER BY g.createdAt DESC
            """)
    List<ChatGroup> findActiveByCursor(@Param("cursor") java.time.LocalDateTime cursor,
                                       Pageable pageable);

    @Query("""
            SELECT g FROM ChatGroup g
            WHERE g.isActive = true
            ORDER BY g.createdAt DESC
            """)
    List<ChatGroup> findActiveFirstPage(Pageable pageable);
    
    boolean existsByName(String name);

    Optional<ChatGroup> findByIdAndIsActiveTrue(UUID id);
}
