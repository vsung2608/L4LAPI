package com.v1no.LJL.learning_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v1no.LJL.learning_service.model.entity.Category;
import com.v1no.LJL.learning_service.model.enums.ContentStatus;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);

    @EntityGraph(value = "Category.simple")
    @Query("SELECT c FROM Category c WHERE c.status = :status ORDER BY c.displayOrder ASC")
    List<Category> findAllByStatus(@Param("status") ContentStatus status);

    @EntityGraph(value = "Category.withLessons")
    @Query("SELECT c FROM Category c WHERE c.status = :status ORDER BY c.displayOrder ASC")
    List<Category> findAllWithLessonsByStatus(@Param("status") ContentStatus status);

    @EntityGraph(value = "Category.withLessonsAndSentences")
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findByIdWithLessonsAndSentences(@Param("id") UUID id);

    @EntityGraph(value = "Category.withLanguage")
    @Query("""
        SELECT c FROM Category c
        WHERE c.language.code = :languageCode
        AND c.status = 'ACTIVE'
        ORDER BY c.displayOrder ASC
        """)
    List<Category> findActiveByLanguageCode(@Param("languageCode") String languageCode);
}
