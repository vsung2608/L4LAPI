package com.v1no.LJL.content_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v1no.LJL.content_service.model.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    @EntityGraph(value = "Article.withCategory")
    @Query("SELECT a FROM Article a WHERE a.slug = :slug")
    Optional<Article> findBySlugWithCategory(@Param("slug") String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    @EntityGraph(value = "Article.withCategory")
    @Query("""
        SELECT a FROM Article a
        WHERE a.status = 'PUBLISHED'
        ORDER BY a.publishedAt DESC
        """)
    Page<Article> findAllPublished(Pageable pageable);

    @EntityGraph(value = "Article.withCategory")
    @Query("""
        SELECT a FROM Article a
        WHERE a.category.id = :categoryId
          AND a.status = 'PUBLISHED'
        ORDER BY a.publishedAt DESC
        """)
    Page<Article> findPublishedByCategoryId(
        @Param("categoryId") UUID categoryId,
        Pageable pageable
    );

    @EntityGraph(value = "Article.withCategory")
    @Query("""
        SELECT a FROM Article a
        WHERE a.authorId = :authorId
        ORDER BY a.createdAt DESC
        """)
    Page<Article> findAllByAuthorId(
        @Param("authorId") UUID authorId,
        Pageable pageable
    );

    @EntityGraph(value = "Article.withCategory")
    @Query("""
        SELECT a FROM Article a
        WHERE a.isFeatured = true
          AND a.status = 'PUBLISHED'
        ORDER BY a.publishedAt DESC
        """)
    List<Article> findFeatured();

    @Modifying
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Article a SET a.likeCount = a.likeCount + 1 WHERE a.id = :id")
    void incrementLikeCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Article a SET a.likeCount = a.likeCount - 1 WHERE a.id = :id AND a.likeCount > 0")
    void decrementLikeCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Article a SET a.commentCount = a.commentCount + 1 WHERE a.id = :id")
    void incrementCommentCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Article a SET a.commentCount = a.commentCount - 1 WHERE a.id = :id AND a.commentCount > 0")
    void decrementCommentCount(@Param("id") UUID id);
}
