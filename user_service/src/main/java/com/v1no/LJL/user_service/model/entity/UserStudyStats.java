package com.v1no.LJL.user_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_study_stats", indexes = {
    @Index(name = "idx_stats_streak", columnList = "current_streak_days"),
    @Index(name = "idx_stats_total_time", columnList = "total_study_time_minutes")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStudyStats {

    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    // Overall stats
    @Column(name = "total_study_time_minutes")
    @Builder.Default
    private Integer totalStudyTimeMinutes = 0;

    @Column(name = "total_lessons_completed")
    @Builder.Default
    private Integer totalLessonsCompleted = 0;

    @Column(name = "total_exercises_done")
    @Builder.Default
    private Integer totalExercisesDone = 0;

    @Column(name = "total_words_learned")
    @Builder.Default
    private Integer totalWordsLearned = 0;

    // Current streak
    @Column(name = "current_streak_days")
    @Builder.Default
    private Integer currentStreakDays = 0;

    @Column(name = "longest_streak_days")
    @Builder.Default
    private Integer longestStreakDays = 0;

    @Column(name = "last_study_date")
    private LocalDate lastStudyDate;

    // Level progress
    @Column(name = "current_level", length = 20)
    @Builder.Default
    private String currentLevel = "N5";

    @Column(name = "level_progress_percent", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal levelProgressPercent = BigDecimal.ZERO;

    // Weekly stats
    @Column(name = "weekly_study_minutes")
    @Builder.Default
    private Integer weeklyStudyMinutes = 0;

    @Column(name = "weekly_lessons_completed")
    @Builder.Default
    private Integer weeklyLessonsCompleted = 0;

    @Column(name = "week_start_date")
    private LocalDate weekStartDate;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateStreak(LocalDate studyDate) {
        if (lastStudyDate == null) {
            currentStreakDays = 1;
            lastStudyDate = studyDate;
        } else {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastStudyDate, studyDate);
            
            if (daysBetween == 1) {
                currentStreakDays++;
                if (currentStreakDays > longestStreakDays) {
                    longestStreakDays = currentStreakDays;
                }
            } else if (daysBetween > 1) {
                currentStreakDays = 1;
            }
            
            lastStudyDate = studyDate;
        }
    }

    public void resetWeeklyStats(LocalDate newWeekStart) {
        this.weeklyStudyMinutes = 0;
        this.weeklyLessonsCompleted = 0;
        this.weekStartDate = newWeekStart;
    }
}