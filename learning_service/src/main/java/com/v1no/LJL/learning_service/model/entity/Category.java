package com.v1no.LJL.learning_service.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories",
    indexes = {
        @Index(name = "idx_category_language_code", columnList = "language_code"),
        @Index(name = "idx_category_status",        columnList = "status")
    }
)
@NamedEntityGraphs({
    @NamedEntityGraph(name = "Category.simple"),
    @NamedEntityGraph(
        name = "Category.withLanguage",
        attributeNodes = @NamedAttributeNode("language")
    ),
    @NamedEntityGraph(
        name = "Category.withLessons",
        attributeNodes = {
            @NamedAttributeNode("language"),
            @NamedAttributeNode("lessons")
        }
    )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = false, length = 10)
    private LanguageCode language;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ContentStatus status = ContentStatus.ACTIVE;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
