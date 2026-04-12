package com.v1no.LJL.content_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.v1no.LJL.content_service.custom.StringListConverter;
import com.v1no.LJL.content_service.model.enums.ArticleStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "articles",
    indexes = {
        @Index(name = "idx_article_slug",         columnList = "slug"),
        @Index(name = "idx_article_author_id",    columnList = "author_id"),
        @Index(name = "idx_article_status",       columnList = "status"),
        @Index(name = "idx_article_published_at", columnList = "published_at"),
        @Index(name = "idx_article_category_id",  columnList = "category_id"),
        @Index(name = "idx_article_is_featured",  columnList = "is_featured")
    }
)
@NamedEntityGraphs({
    @NamedEntityGraph(name = "Article.simple"),
    @NamedEntityGraph(
        name = "Article.withCategory",
        attributeNodes = @NamedAttributeNode("category")
    )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ArticleCategory category;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, unique = true, length = 300)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "meta_title", length = 200)
    private String metaTitle;

    @Column(name = "meta_description", length = 160)
    private String metaDescription;

    @Convert(converter = StringListConverter.class)
    @Column(name = "meta_keywords", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> metaKeywords = new ArrayList<>();

    @Column(name = "featured_image", length = 500)
    private String featuredImage;

    @Column(name = "thumbnail_image", length = 500)
    private String thumbnailImage;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ArticleStatus status = ArticleStatus.DRAFT;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    @Column(name = "estimated_read_minutes", nullable = false)
    @Builder.Default
    private Integer estimatedReadMinutes = 1;

    @Column(name = "allow_comments", nullable = false)
    @Builder.Default
    private Boolean allowComments = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void publish() {
        if (ArticleStatus.ARCHIVED.equals(this.status)) {
            throw new IllegalStateException("Cannot publish an archived article");
        }
        this.status = ArticleStatus.PUBLISHED;
        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void archive() {
        if (ArticleStatus.DRAFT.equals(this.status)) {
            throw new IllegalStateException("Cannot archive a draft article");
        }
        this.status = ArticleStatus.ARCHIVED;
    }

    // Tính estimated read dựa trên content — 200 words/phút
    public void recalculateReadTime() {
        if (this.content == null || this.content.isBlank()) {
            this.estimatedReadMinutes = 1;
            return;
        }
        int wordCount = this.content.trim().split("\\s+").length;
        this.estimatedReadMinutes = Math.max(1, (int) Math.ceil(wordCount / 200.0));
    }
}
