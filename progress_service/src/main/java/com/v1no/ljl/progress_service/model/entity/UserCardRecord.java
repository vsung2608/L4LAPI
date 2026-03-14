package com.v1no.ljl.progress_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.v1no.ljl.progress_service.model.enums.CardMark;

@Entity
@Table(
    name = "user_card_records",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "flash_card_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCardRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "flash_card_id", nullable = false)
    private UUID flashCardId;

    @Column(name = "study_count", nullable = false)
    @Builder.Default
    private Integer studyCount = 1;

    @Enumerated(EnumType.STRING)
    private CardMark mark;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_deck_progress_id", nullable = false)
    @JsonBackReference
    private UserDeckProgress userDeckProgress;

    @Column(name = "studied_at", nullable = false)
    private Instant studiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
