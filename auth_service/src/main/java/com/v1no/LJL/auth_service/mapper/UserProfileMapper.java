package com.v1no.LJL.auth_service.mapper;

import com.v1no.LJL.auth_service.model.dto.request.UpdateProfileRequest;
import com.v1no.LJL.auth_service.model.dto.request.UpdateSettingsRequest;
import com.v1no.LJL.auth_service.model.dto.response.UserProfileDetailResponse;
import com.v1no.LJL.auth_service.model.dto.response.UserProfileSummaryResponse;
import com.v1no.LJL.auth_service.model.dto.response.VipStatusResponse;
import com.v1no.LJL.auth_service.model.entity.UserCredential;
import com.v1no.LJL.auth_service.model.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    /**
     * Map to Summary Response
     */
    public UserProfileSummaryResponse toSummaryResponse(UserProfile profile) {
        if (profile == null) {
            return null;
        }

        UserCredential credential = profile.getUserCredential();

        return new UserProfileSummaryResponse(
            profile.getId(),
            credential != null ? credential.getUsername() : null,
            credential != null ? credential.getUsername() : null,
            profile.getDisplayName(),
            profile.getAvatarUrl(),
            profile.getIsVip(),
            profile.isVipActive(),
            profile.getVipExpiresAt(),
            profile.getVipDaysRemaining(),
            profile.getCurrentStreakDays(),
            profile.getTotalLessonsCompleted(),
            profile.getTotalWordsLearned(),
            profile.getProfileCompletionPercentage(),
            profile.getLastLoginAt()
        );
    }

    /**
     * Map to Detail Response
     */
    public UserProfileDetailResponse toDetailResponse(UserProfile profile) {
        if (profile == null) {
            return null;
        }

        UserCredential credential = profile.getUserCredential();

        return new UserProfileDetailResponse(
            profile.getId(),
            credential != null ? credential.getUsername() : null,
            profile.getDisplayName(),
            profile.getAvatarUrl(),
            profile.getBio(),
            profile.getDateOfBirth(),
            profile.getGender() != null ? profile.getGender().name() : null,
            profile.getStudyGoal() != null ? profile.getStudyGoal().name() : null,
            profile.getIsVip(),
            profile.isVipActive(),
            profile.isVipLifetime(),
            profile.getVipExpiresAt(),
            profile.getVipStartedAt(),
            profile.getVipDaysRemaining(),
            profile.getDailyGoalMinutes(),
            profile.getProfileVisibility() != null ? profile.getProfileVisibility().name() : null,
            profile.getShowLearningProgress(),
            profile.getAllowFriendRequests(),
            profile.getShowOnlineStatus(),
            profile.getTotalStudyTimeMinutes(),
            profile.getTotalLessonsCompleted(),
            profile.getTotalWordsLearned(),
            profile.getCurrentStreakDays(),
            profile.getLongestStreakDays(),
            profile.getLastStudyDate(),
            profile.getPhoneNumber(),
            profile.getFacebook(),
            profile.getInstagram(),
            profile.getTwitter(),
            profile.getCountry(),
            profile.getCity(),
            profile.getProfileCompletionPercentage(),
            profile.getCreatedAt(),
            profile.getUpdatedAt(),
            profile.getLastLoginAt()
        );
    }

    public VipStatusResponse toVipStatusResponse(UserProfile profile) {
        if (profile == null) {
            return new VipStatusResponse(
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                "NONE"
            );
        }

        String status;
        if (!profile.getIsVip()) {
            status = "NONE";
        } else if (profile.isVipExpired()) {
            status = "EXPIRED";
        } else {
            status = "ACTIVE";
        }

        return new VipStatusResponse(
            profile.getIsVip(),
            profile.isVipActive(),
            profile.isVipLifetime(),
            null,
            profile.getVipStartedAt(),
            profile.getVipExpiresAt(),
            profile.getVipDaysRemaining(),
            status
        );
    }

    public void updateProfileFromRequest(UserProfile profile, UpdateProfileRequest request) {
        if (request.displayName() != null) {
            profile.setDisplayName(request.displayName());
        }
        if (request.avatarUrl() != null) {
            profile.setAvatarUrl(request.avatarUrl());
        }
        if (request.bio() != null) {
            profile.setBio(request.bio());
        }
        if (request.dateOfBirth() != null) {
            profile.setDateOfBirth(request.dateOfBirth());
        }
        if (request.gender() != null) {
            profile.setGender(UserProfile.Gender.valueOf(request.gender()));
        }
        if (request.studyGoal() != null) {
            profile.setStudyGoal(UserProfile.StudyGoal.valueOf(request.studyGoal()));
        }
        if (request.phoneNumber() != null) {
            profile.setPhoneNumber(request.phoneNumber());
        }
        if (request.facebook() != null) {
            profile.setFacebook(request.facebook());
        }
        if (request.instagram() != null) {
            profile.setInstagram(request.instagram());
        }
        if (request.twitter() != null) {
            profile.setTwitter(request.twitter());
        }
        if (request.country() != null) {
            profile.setCountry(request.country());
        }
        if (request.city() != null) {
            profile.setCity(request.city());
        }
    }

    public void updateSettingsFromRequest(UserProfile profile, UpdateSettingsRequest request) {
        if (request.dailyGoalMinutes() != null) {
            profile.setDailyGoalMinutes(request.dailyGoalMinutes());
        }

        if (request.profileVisibility() != null) {
            profile.setProfileVisibility(UserProfile.ProfileVisibility.valueOf(request.profileVisibility()));
        }
        if (request.showLearningProgress() != null) {
            profile.setShowLearningProgress(request.showLearningProgress());
        }
        if (request.allowFriendRequests() != null) {
            profile.setAllowFriendRequests(request.allowFriendRequests());
        }
        if (request.showOnlineStatus() != null) {
            profile.setShowOnlineStatus(request.showOnlineStatus());
        }
    }
}