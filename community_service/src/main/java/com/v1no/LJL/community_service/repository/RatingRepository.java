package com.v1no.LJL.community_service.repository;

import com.v1no.LJL.community_service.model.entity.Rating;
import com.v1no.LJL.community_service.model.enums.CommentTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByUserIdAndTargetType(
            UUID userId,
            CommentTargetType targetType
    );

    boolean existsByUserIdAndTargetType(
            UUID userId,
            CommentTargetType targetType
    );

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.targetType = :type")
    Double findAverageScoreByTarget(
            @Param("type") CommentTargetType targetType
    );

    long countByTargetType(CommentTargetType targetType);

    long countByTargetTypeAndScore(CommentTargetType targetType, Integer score);

    Page<Rating> findByUserId(UUID userId, Pageable pageable);

    void deleteByUserIdAndTargetType(
            UUID userId,
            CommentTargetType targetType
    );

    Page<Rating> findByTargetType(CommentTargetType targetType, Pageable pageable);
}