package com.v1no.LJL.auth_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles", indexes = {
    @Index(name = "idx_user_profile_vip", columnList = "is_vip, vip_expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserCredential userCredential;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "study_goal")
    @Enumerated(EnumType.STRING)
    private StudyGoal studyGoal;

    @Column(name = "is_vip", nullable = false)
    @Builder.Default
    private Boolean isVip = false;

    @Column(name = "vip_expires_at")
    private LocalDateTime vipExpiresAt;

    @Column(name = "vip_started_at")
    private LocalDateTime vipStartedAt;

    @Column(name = "daily_goal_minutes")
    @Builder.Default
    private Integer dailyGoalMinutes = 30;

    @Column(name = "profile_visibility", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    @Column(name = "show_learning_progress", nullable = false)
    @Builder.Default
    private Boolean showLearningProgress = true;

    @Column(name = "allow_friend_requests", nullable = false)
    @Builder.Default
    private Boolean allowFriendRequests = true;

    @Column(name = "show_online_status", nullable = false)
    @Builder.Default
    private Boolean showOnlineStatus = true;

    @Column(name = "total_study_time_minutes")
    @Builder.Default
    private Integer totalStudyTimeMinutes = 0;

    @Column(name = "total_lessons_completed")
    @Builder.Default
    private Integer totalLessonsCompleted = 0;

    @Column(name = "total_words_learned")
    @Builder.Default
    private Integer totalWordsLearned = 0;

    @Column(name = "current_streak_days")
    @Builder.Default
    private Integer currentStreakDays = 0;

    @Column(name = "longest_streak_days")
    @Builder.Default
    private Integer longestStreakDays = 0;

    @Column(name = "last_study_date")
    private LocalDate lastStudyDate;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;


    @Column(length = 100)
    private String facebook;

    @Column(length = 100)
    private String instagram;

    @Column(length = 100)
    private String twitter;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public enum Gender {
        MALE,
        FEMALE,
        OTHER,
        PREFER_NOT_TO_SAY
    }

    public enum Theme {
        LIGHT,
        DARK,
        AUTO
    }

    public enum ProfileVisibility {
        PUBLIC,
        FRIENDS,
        PRIVATE
    }

    public enum StudyGoal {
        JLPT_EXAM,
        TRAVEL,
        WORK,
        STUDY_ABROAD,
        ANIME_MANGA,
        HOBBY,
        BUSINESS,
        MAKE_FRIENDS,
        OTHER
    }

    public boolean isVipActive() {
        if (!Boolean.TRUE.equals(isVip)) {
            return false;
        }
        
        // Lifetime VIP
        if (vipExpiresAt == null) {
            return true;
        }
        
        // Check expiry
        return LocalDateTime.now().isBefore(vipExpiresAt);
    }

    public boolean isVipExpired() {
        return Boolean.TRUE.equals(isVip) && 
               vipExpiresAt != null && 
               LocalDateTime.now().isAfter(vipExpiresAt);
    }

    public boolean isVipLifetime() {
        return Boolean.TRUE.equals(isVip) && vipExpiresAt == null;
    }

    public Long getVipDaysRemaining() {
        if (!Boolean.TRUE.equals(isVip) || vipExpiresAt == null) {
            return null;
        }
        
        if (LocalDateTime.now().isAfter(vipExpiresAt)) {
            return 0L;
        }
        
        return java.time.temporal.ChronoUnit.DAYS.between(
            LocalDateTime.now(), 
            vipExpiresAt
        );
    }

    public void upgradeToVip(String planCode, Integer durationDays) {
        this.isVip = true;
        this.vipStartedAt = LocalDateTime.now();
        
        if (durationDays != null) {
            this.vipExpiresAt = LocalDateTime.now().plusDays(durationDays);
        } else {
            this.vipExpiresAt = null;
        }
    }

    public void extendVip(Integer durationDays) {
        if (vipExpiresAt == null) {
            return;
        }
        
        LocalDateTime baseDate = LocalDateTime.now().isAfter(vipExpiresAt) 
            ? LocalDateTime.now() 
            : vipExpiresAt;
            
        this.vipExpiresAt = baseDate.plusDays(durationDays);
    }

    public void expireVip() {
        this.isVip = false;
        this.vipExpiresAt = null;
    }

    public void cancelVip() {
        // VIP will expire at vipExpiresAt, don't renew
        // For now, just a marker. Could add cancelledAt field if needed.
    }

    // ========== STUDY HELPER METHODS ==========
    
    public void updateStreak() {
        LocalDate today = LocalDate.now();
        
        if (lastStudyDate == null) {
            currentStreakDays = 1;
            longestStreakDays = 1;
            lastStudyDate = today;
            return;
        }
        
        if (lastStudyDate.equals(today)) {
            return;
        }
        
        if (lastStudyDate.equals(today.minusDays(1))) {
            currentStreakDays++;
            if (currentStreakDays > longestStreakDays) {
                longestStreakDays = currentStreakDays;
            }
        } else {
            currentStreakDays = 1;
        }
        
        lastStudyDate = today;
    }

    public void addStudyTime(int minutes) {
        this.totalStudyTimeMinutes += minutes;
        updateStreak();
    }

    public void incrementLessonsCompleted() {
        this.totalLessonsCompleted++;
    }

    public void addWordsLearned(int count) {
        this.totalWordsLearned += count;
    }

    public int getProfileCompletionPercentage() {
        int total = 0;
        int filled = 0;

        total++;
        filled++;

        total++; if (displayName != null && !displayName.isBlank()) filled++;
        total++; if (avatarUrl != null && !avatarUrl.isBlank()) filled++;
        total++; if (bio != null && !bio.isBlank()) filled++;
        total++; if (dateOfBirth != null) filled++;
        total++; if (gender != null) filled++;
        total++; if (studyGoal != null) filled++;
        total++; if (phoneNumber != null) filled++;

        return (filled * 100) / total;
    }

    public boolean canReceiveFriendRequests() {
        return Boolean.TRUE.equals(allowFriendRequests);
    }

    public boolean isVisibleTo(UserProfile viewer) {
        if (viewer == null) {
            return ProfileVisibility.PUBLIC.equals(this.profileVisibility);
        }

        if (viewer.getId().equals(this.id)) {
            return true;
        }

        return switch (this.profileVisibility) {
            case PUBLIC -> true;
            case FRIENDS -> false;
            case PRIVATE -> false;
        };
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public String getDisplayNameOrEmail() {
        if (displayName != null && !displayName.isBlank()) {
            return displayName;
        }
        return userCredential != null ? userCredential.getUsername() : "User";
    }

    public boolean hasDailyGoal() {
        return dailyGoalMinutes != null && dailyGoalMinutes > 0;
    }
}
