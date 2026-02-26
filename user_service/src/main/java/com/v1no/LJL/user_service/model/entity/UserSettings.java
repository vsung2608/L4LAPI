package com.v1no.LJL.user_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.v1no.LJL.user_service.model.enums.ProfileVisibility;
import com.v1no.LJL.user_service.model.enums.Theme;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettings {

    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    @Column(name = "email_notifications")
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(name = "push_notifications")
    @Builder.Default
    private Boolean pushNotifications = true;

    @Column(name = "daily_reminder_enabled")
    @Builder.Default
    private Boolean dailyReminderEnabled = true;

    @Column(name = "daily_reminder_time")
    @Builder.Default
    private LocalTime dailyReminderTime = LocalTime.of(19, 0);

    @Column(name = "daily_goal_minutes")
    @Builder.Default
    private Integer dailyGoalMinutes = 30;

    @Column(name = "auto_play_audio")
    @Builder.Default
    private Boolean autoPlayAudio = true;

    @Column(name = "show_romaji")
    @Builder.Default
    private Boolean showRomaji = true;

    @Column(name = "show_furigana")
    @Builder.Default
    private Boolean showFurigana = true;

    @Column(name = "profile_visibility", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    @Column(name = "show_learning_progress")
    @Builder.Default
    private Boolean showLearningProgress = true;

    @Column(name = "allow_friend_requests")
    @Builder.Default
    private Boolean allowFriendRequests = true;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Theme theme = Theme.LIGHT;

    @Column(length = 10)
    @Builder.Default
    private String language = "vi";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
