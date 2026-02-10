package com.v1no.LJL.user_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "study_sessions", indexes = {
    @Index(name = "idx_sessions_user", columnList = "user_id, started_at"),
    @Index(name = "idx_sessions_date", columnList = "started_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "activity_type", length = 50)
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Column(name = "items_completed")
    @Builder.Default
    private Integer itemsCompleted = 0;

    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0;

    public enum ActivityType {
        VOCABULARY,
        SPELLING,
        SHADOWING,
        READING,
        GRAMMAR,
        LISTENING
    }

    public void endSession() {
        this.endedAt = LocalDateTime.now();
        this.durationMinutes = (int) java.time.Duration.between(startedAt, endedAt).toMinutes();
    }
}
