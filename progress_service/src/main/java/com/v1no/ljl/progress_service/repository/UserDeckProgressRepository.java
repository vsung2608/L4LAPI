package com.v1no.ljl.progress_service.repository;

import com.v1no.ljl.progress_service.model.entity.UserDeckProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDeckProgressRepository extends JpaRepository<UserDeckProgress, UUID> {

    Optional<UserDeckProgress> findByUserIdAndDeckId(UUID userId, UUID deckId);

    @Query("""
            SELECT p FROM UserDeckProgress p
            LEFT JOIN FETCH p.records
            WHERE p.userId = :userId AND p.deckId = :deckId
            """)
    Optional<UserDeckProgress> findWithCardsByUserIdAndDeckId(
            @Param("userId") UUID userId,
            @Param("deckId") UUID deckId
    );

    void deleteByUserIdAndDeckId(UUID userId, UUID deckId);

    List<UserDeckProgress> findAllByUserIdAndDeckIdIn(UUID userId, List<UUID> deckIds);

}