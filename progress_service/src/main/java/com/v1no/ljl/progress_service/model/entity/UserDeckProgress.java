package com.v1no.ljl.progress_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.v1no.ljl.progress_service.model.enums.CardMark;

@Entity
@Table(
    name = "user_deck_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "deck_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeckProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "deck_id", nullable = false)
    private UUID deckId;

    @Column(name = "last_studied_card_id")
    private Long lastStudiedCardId;

    @Column(name = "total_studied", nullable = false)
    @Builder.Default
    private Integer totalStudied = 0;

    @OneToMany(mappedBy = "userDeckProgress")
    @JsonManagedReference
    @Builder.Default
    private List<UserCardRecord> records = new ArrayList<>();

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