package com.v1no.LJL.learning_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v1no.LJL.learning_service.model.entity.Lesson;
import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.model.enums.JlptLevel;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    @EntityGraph(value = "Lesson.withCategory")
    @Query("SELECT l FROM Lesson l WHERE l.id = :id")
    Optional<Lesson> findByIdWithCategory(@Param("id") UUID id);

    @EntityGraph(value = "Lesson.simple")
    @Query("""
        SELECT l FROM Lesson l
        WHERE l.category.id = :categoryId
        ORDER BY l.displayOrder ASC
        """)
    List<Lesson> findAllByCategoryId(@Param("categoryId") UUID categoryId);

    @EntityGraph(value = "Lesson.withSentences")
    @Query("SELECT l FROM Lesson l WHERE l.id = :id")
    Optional<Lesson> findActiveByIdWithSentences(@Param("id") UUID id);

    @EntityGraph(value = "Lesson.full")
    @Query("SELECT l FROM Lesson l WHERE l.id = :id")
    Optional<Lesson> findByIdFull(@Param("id") UUID id);

    @EntityGraph(value = "Lesson.withCategory")
    @Query("""
        SELECT l FROM Lesson l
        WHERE l.level = :level
          AND l.status = 'ACTIVE'
        ORDER BY l.displayOrder ASC
        """)
    List<Lesson> findActiveByLevelWithCategory(@Param("level") JlptLevel level);

    @EntityGraph(value = "Lesson.simple")
    @Query("""
        SELECT l FROM Lesson l
        WHERE l.status = 'ACTIVE'
        ORDER BY l.createdAt DESC
        """)
    Page<Lesson> findAllActive(Pageable pageable);

    @EntityGraph(value = "Lesson.withCategory")
    @Query("""
        SELECT l FROM Lesson l
        WHERE l.category.id IN :categoryIds
        ORDER BY l.category.id ASC, l.displayOrder ASC
        """)
    List<Lesson> findActiveByCategoryIdIn(@Param("categoryIds") List<UUID> categoryIds);

}
