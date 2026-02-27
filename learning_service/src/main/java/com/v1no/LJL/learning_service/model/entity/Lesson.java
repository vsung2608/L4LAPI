package com.v1no.LJL.learning_service.model.entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.model.enums.JlptLevel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons", indexes = {
    @Index(name = "idx_lesson_category_id", columnList = "category_id"),
    @Index(name = "idx_lesson_level",       columnList = "level"),
    @Index(name = "idx_lesson_status",      columnList = "status")
})
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "Lesson.simple"
    ),
    @NamedEntityGraph(
        name = "Lesson.withSentences",
        attributeNodes = {
            @NamedAttributeNode("sentences")
        }
    ),
    @NamedEntityGraph(
        name = "Lesson.withCategory",
        attributeNodes = {
            @NamedAttributeNode("category")
        }
    ),
    @NamedEntityGraph(
        name = "Lesson.full",
        attributeNodes = {
            @NamedAttributeNode("category"),
            @NamedAttributeNode(value = "sentences")
        }
    )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "youtube_video_id", length = 50)
    private String youtubeVideoId;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "level", nullable = false, length = 20)
    private String level;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ContentStatus status = ContentStatus.DRAFT;

    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY,
               cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<Sentence> sentences = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public int getTotalSentences() {
        return sentences.size();
    }
}
