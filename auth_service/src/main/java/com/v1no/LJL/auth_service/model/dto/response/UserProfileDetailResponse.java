package com.v1no.LJL.auth_service.model.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileDetailResponse(
    
    UUID id,
    
    String email,
    String displayName,
    String avatarUrl,
    String bio,
    LocalDate dateOfBirth,
    String gender,
    String studyGoal,
    
    Boolean isVip,
    Boolean isVipActive,
    Boolean isVipLifetime,
    LocalDateTime vipExpiresAt,
    LocalDateTime vipStartedAt,
    Long vipDaysRemaining,
    
    Integer dailyGoalMinutes,
    
    String profileVisibility,
    Boolean showLearningProgress,
    Boolean allowFriendRequests,
    Boolean showOnlineStatus,
    
    Integer totalStudyTimeMinutes,
    Integer totalLessonsCompleted,
    Integer totalWordsLearned,
    Integer currentStreakDays,
    Integer longestStreakDays,
    LocalDate lastStudyDate,
    
    String phoneNumber,
    String facebook,
    String instagram,
    String twitter,
    
    String country,
    String city,
    
    Integer profileCompletionPercentage,
    
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLoginAt
) {
}
