package com.v1no.LJL.auth_service.service;

import java.util.UUID;

import com.v1no.LJL.auth_service.model.dto.request.UpdateProfileRequest;
import com.v1no.LJL.auth_service.model.dto.request.UpdateSettingsRequest;
import com.v1no.LJL.auth_service.model.dto.response.UserProfileDetailResponse;
import com.v1no.LJL.auth_service.model.dto.response.UserProfileSummaryResponse;
import com.v1no.LJL.auth_service.model.dto.response.VipStatusResponse;
import com.v1no.LJL.auth_service.model.entity.UserCredential;
import com.v1no.LJL.auth_service.model.entity.UserProfile;

public interface UserProfileSerivce {
    void upgradeToVip(UUID userId, String planCode, int duration);
    UserProfileSummaryResponse getProfileSummary(UUID userId);
    UserProfileDetailResponse getProfileDetail(UUID userId);
    UserProfileDetailResponse updateProfile(UUID userId, UpdateProfileRequest request);
    UserProfileDetailResponse updateSettings(UUID userId, UpdateSettingsRequest request);
    VipStatusResponse getVipStatus(UUID userId);
    void upgradeToVip(UUID userId, String planCode, Integer durationDays);
    VipStatusResponse extendVip(UUID userId, Integer durationDays);
    VipStatusResponse cancelVip(UUID userId);
    UserProfile createProfile(UserCredential credential);
    void updateLastLogin(UUID userId);
    void addStudyTime(UUID userId, int minutes);
    void incrementLessonsCompleted(UUID userId);
    void addWordsLearned(UUID userId, int count);
}
