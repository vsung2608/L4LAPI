package com.v1no.LJL.content_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "articles", indexes = {
    @Index(name = "idx_article_slug", columnList = "slug"),
    @Index(name = "idx_article_category", columnList = "category_id"),
    @Index(name = "idx_article_author", columnList = "author_id"),
    @Index(name = "idx_article_published", columnList = "published_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
    name = "Article.withCategory",
    attributeNodes = @NamedAttributeNode("category")
)
@NamedEntityGraph(
    name = "Article.withTags",
    attributeNodes = @NamedAttributeNode("articleTagRelations")
)
@NamedEntityGraph(
    name = "Article.full",
    attributeNodes = {
        @NamedAttributeNode("category"),
        @NamedAttributeNode(value = "articleTagRelations", subgraph = "tags")
    },
    subgraphs = @NamedSubgraph(name = "tags", attributeNodes = @NamedAttributeNode("tag"))
)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;

    @Column(name = "featured_image", length = 500)
    private String featuredImage;

    @Column(name = "thumbnail_image", length = 500)
    private String thumbnailImage;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "comment_count")
    @Builder.Default
    private Integer commentCount = 0;

    @Column(name = "estimated_read_minutes")
    @Builder.Default
    private Integer estimatedReadMinutes = 5;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "allow_comments", nullable = false)
    @Builder.Default
    private Boolean allowComments = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }

    // Helper methods
    public void publish() {
        this.status = Status.PUBLISHED;
        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
