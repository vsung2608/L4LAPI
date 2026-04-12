package com.v1no.ljl.progress_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.v1no.ljl.progress_service.model.dto.response.DailyLessonSummary;
import com.v1no.ljl.progress_service.model.entity.UserLessonProgress;
import com.v1no.ljl.progress_service.model.enums.LearningMode;
import com.v1no.ljl.progress_service.model.enums.LessonStatus;

import feign.Param;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, UUID> {

    Optional<UserLessonProgress> findByUserIdAndLessonIdAndMode(
        UUID userId, UUID lessonId, LearningMode mode
    );

    List<UserLessonProgress> findAllByUserIdAndLessonId(UUID userId, UUID lessonId);

    List<UserLessonProgress> findAllByUserIdOrderByLastAccessedAtDesc(UUID userId);

    @Query("""
        SELECT p FROM UserLessonProgress p
        WHERE p.userId = :userId AND p.status = :status
        ORDER BY p.lastAccessedAt DESC
        """)
    List<UserLessonProgress> findAllByUserIdAndStatus(
        @Param("userId") UUID userId,
        @Param("status") LessonStatus status
    );

    @Query("""
        SELECT p FROM UserLessonProgress p
        WHERE p.userId = :userId
          AND p.lessonId IN :lessonIds
        """)
    List<UserLessonProgress> findAllByUserIdAndLessonIdIn(
        @Param("userId") UUID userId,
        @Param("lessonIds") List<UUID> lessonIds
    );

    boolean existsByUserIdAndLessonIdAndMode(UUID userId, UUID lessonId, LearningMode mode);

   @Query(value = """
        SELECT
            d.study_date,
            COUNT(ulp.lesson_id)                                 AS total_lessons,
            COUNT(CASE WHEN ulp.mode = 'DICTATION' THEN 1 END)  AS total_dictation,
            COUNT(CASE WHEN ulp.mode = 'SHADOWING' THEN 1 END)  AS total_shadowing
        FROM (
            SELECT CURRENT_DATE - INTERVAL '1 day' * s AS study_date
            FROM generate_series(0, 6) s
        ) d
        LEFT JOIN user_lesson_progress ulp
            ON DATE(ulp.started_at) = d.study_date
            AND ulp.user_id = :userId
        GROUP BY d.study_date
        ORDER BY d.study_date DESC
        """, nativeQuery = true)
    List<DailyLessonSummary> findDailyLessonCount(@Param("userId") UUID userId);
}