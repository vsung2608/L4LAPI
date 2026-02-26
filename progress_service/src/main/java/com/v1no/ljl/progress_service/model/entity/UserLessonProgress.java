package com.v1no.ljl.progress_service.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.v1no.ljl.progress_service.model.enums.LessonStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "user_lesson_progress",
    indexes = {
        @Index(name = "idx_ulp_user_id",           columnList = "user_id"),
        @Index(name = "idx_ulp_user_status",        columnList = "user_id, status"),
        @Index(name = "idx_ulp_user_last_accessed", columnList = "user_id, last_accessed_at")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_ulp_user_lesson",
            columnNames = {"user_id", "lesson_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "lesson_id", nullable = false)
    private UUID lessonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private LessonStatus status = LessonStatus.IN_PROGRESS;

    @Column(name = "current_sentence_index", nullable = false)
    @Builder.Default
    private Integer currentSentenceIndex = 0;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @UpdateTimestamp
    @Column(name = "last_accessed_at", nullable = false)
    private LocalDateTime lastAccessedAt;

    public void advanceTo(int sentenceIndex) {
        if (sentenceIndex < 0) {
            throw new IllegalArgumentException("Sentence index must be >= 0");
        }
        this.currentSentenceIndex = sentenceIndex;
    }

    public void complete() {
        this.status = LessonStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return LessonStatus.COMPLETED.equals(this.status);
    }
}