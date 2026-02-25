package com.v1no.LJL.learning_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v1no.LJL.learning_service.model.entity.Sentence;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, UUID> {

    @EntityGraph(attributePaths = {"lesson"})
    @Query("SELECT s FROM Sentence s WHERE s.id = :id")
    Optional<Sentence> findByIdWithLesson(@Param("id") UUID id);

    @Query("""
        SELECT s FROM Sentence s
        WHERE s.lesson.id = :lessonId
        ORDER BY s.orderIndex ASC
        """)
    List<Sentence> findAllByLessonIdOrdered(@Param("lessonId") UUID lessonId);

    boolean existsByLessonIdAndOrderIndex(UUID lessonId, Integer orderIndex);

    boolean existsByLessonIdAndOrderIndexAndIdNot(UUID lessonId, Integer orderIndex, UUID id);
}
