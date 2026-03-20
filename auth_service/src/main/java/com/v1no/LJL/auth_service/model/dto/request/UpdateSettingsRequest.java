package com.v1no.LJL.auth_service.model.dto.request;

import jakarta.validation.constraints.*;

public record UpdateSettingsRequest(
    
    Boolean emailNotifications,
    
    Boolean pushNotifications,
    
    Boolean dailyReminderEnabled,
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format. Use HH:mm")
    String dailyReminderTime,
    
    @Min(value = 1, message = "Daily goal must be at least 1 minute")
    @Max(value = 1440, message = "Daily goal cannot exceed 1440 minutes (24 hours)")
    Integer dailyGoalMinutes,
    
    Boolean autoPlayAudio,
    
    @DecimalMin(value = "0.5", message = "Playback speed must be at least 0.5x")
    @DecimalMax(value = "2.0", message = "Playback speed cannot exceed 2.0x")
    Double playbackSpeed,
    
    String profileVisibility,
    
    Boolean showLearningProgress,
    
    Boolean allowFriendRequests,
    
    Boolean showOnlineStatus
) {
}