package com.v1no.LJL.auth_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileSummaryResponse(
    
    UUID id,
    
    String email,
    String username,
    String displayName,
    String avatarUrl,
    
    Boolean isVip,
    Boolean isVipActive,
    LocalDateTime vipExpiresAt,
    Long vipDaysRemaining,
    
    Integer currentStreakDays,
    Integer totalLessonsCompleted,
    Integer totalWordsLearned,

    Integer profileCompletionPercentage,
    
    LocalDateTime lastLoginAt
) {
}