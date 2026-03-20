package com.v1no.LJL.auth_service.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.auth_service.mapper.UserProfileMapper;
import com.v1no.LJL.auth_service.model.dto.request.UpdateProfileRequest;
import com.v1no.LJL.auth_service.model.dto.request.UpdateSettingsRequest;
import com.v1no.LJL.auth_service.model.dto.response.UserProfileDetailResponse;
import com.v1no.LJL.auth_service.model.dto.response.UserProfileSummaryResponse;
import com.v1no.LJL.auth_service.model.dto.response.VipStatusResponse;
import com.v1no.LJL.auth_service.model.entity.UserCredential;
import com.v1no.LJL.auth_service.model.entity.UserProfile;
import com.v1no.LJL.auth_service.repository.UserCredentialRepository;
import com.v1no.LJL.auth_service.repository.UserProfileRepository;
import com.v1no.LJL.auth_service.service.UserProfileSerivce;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileSerivce{

    private final UserProfileRepository profileRepository;
    private final UserCredentialRepository credentialRepository;
    private final UserProfileMapper profileMapper;

    @Transactional
    public void upgradeToVip(UUID userId, String planCode, int duration){
        UserProfile profile = profileRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Cannot find user with ID::%s to upgrade vip".formatted(userId.toString())));
        
        profile.upgradeToVip(planCode, duration);
    }

     @Transactional(readOnly = true)
    public UserProfileSummaryResponse getProfileSummary(UUID userId) {
        log.debug("Getting profile summary for user: {}", userId);

        UserProfile profile = profileRepository.findByIdWithCredential(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        return profileMapper.toSummaryResponse(profile);
    }

    @Transactional(readOnly = true)
    public UserProfileDetailResponse getProfileDetail(UUID userId) {
        log.debug("Getting profile detail for user: {}", userId);

        UserProfile profile = profileRepository.findByIdWithCredential(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        return profileMapper.toDetailResponse(profile);
    }

    public UserProfileDetailResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", userId);

        UserProfile profile = profileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        profileMapper.updateProfileFromRequest(profile, request);
        profile = profileRepository.save(profile);

        log.info("Profile updated successfully for user: {}", userId);
        return profileMapper.toDetailResponse(profile);
    }

    public UserProfileDetailResponse updateSettings(UUID userId, UpdateSettingsRequest request) {
        log.info("Updating settings for user: {}", userId);

        UserProfile profile = profileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        profileMapper.updateSettingsFromRequest(profile, request);
        profile = profileRepository.save(profile);

        log.info("Settings updated successfully for user: {}", userId);
        return profileMapper.toDetailResponse(profile);
    }

    @Transactional(readOnly = true)
    public VipStatusResponse getVipStatus(UUID userId) {
        log.debug("Getting VIP status for user: {}", userId);

        UserProfile profile = profileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        return profileMapper.toVipStatusResponse(profile);
    }

    public void upgradeToVip(UUID userId, String planCode, Integer durationDays) {
        log.info("Upgrading user to VIP: userId={}, planCode={}, duration={}", 
            userId, planCode, durationDays);

        UserProfile profile = profileRepository.findById(userId)
            .orElse(null);

        if (profile == null) {
            // Create profile if not exists (OAuth users might not have profile yet)
            UserCredential credential = credentialRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            profile = UserProfile.builder()
                .userCredential(credential)
                .build();
        }

        profile.upgradeToVip(planCode, durationDays);
        profileRepository.save(profile);

        log.info("User upgraded to VIP successfully: userId={}", userId);
    }

    public VipStatusResponse extendVip(UUID userId, Integer durationDays) {
        log.info("Extending VIP for user: userId={}, duration={}", userId, durationDays);

        UserProfile profile = profileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        if (!profile.getIsVip()) {
            throw new RuntimeException("User is not VIP");
        }

        profile.extendVip(durationDays);
        profileRepository.save(profile);

        log.info("VIP extended successfully for user: {}", userId);
        return profileMapper.toVipStatusResponse(profile);
    }

    public VipStatusResponse cancelVip(UUID userId) {
        log.info("Cancelling VIP for user: {}", userId);

        UserProfile profile = profileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        if (!profile.getIsVip()) {
            throw new RuntimeException("User is not VIP");
        }

        profile.cancelVip();
        profileRepository.save(profile);

        log.info("VIP cancelled for user: {}", userId);
        return profileMapper.toVipStatusResponse(profile);
    }

    public UserProfile createProfile(UserCredential credential) {
        log.info("Creating profile for user: {}", credential.getId());

        UserProfile profile = UserProfile.builder()
            .userCredential(credential)
            .displayName(credential.getUsername())
            .build();

        profile = profileRepository.save(profile);

        log.info("Profile created successfully for user: {}", credential.getId());
        return profile;
    }

    public void updateLastLogin(UUID userId) {
        profileRepository.findById(userId).ifPresent(profile -> {
            profile.updateLastLogin();
            profileRepository.save(profile);
        });
    }

    public void addStudyTime(UUID userId, int minutes) {
        profileRepository.findById(userId).ifPresent(profile -> {
            profile.addStudyTime(minutes);
            profileRepository.save(profile);
            log.debug("Added {} minutes study time for user: {}", minutes, userId);
        });
    }

    public void incrementLessonsCompleted(UUID userId) {
        profileRepository.findById(userId).ifPresent(profile -> {
            profile.incrementLessonsCompleted();
            profileRepository.save(profile);
            log.debug("Incremented lessons completed for user: {}", userId);
        });
    }

    public void addWordsLearned(UUID userId, int count) {
        profileRepository.findById(userId).ifPresent(profile -> {
            profile.addWordsLearned(count);
            profileRepository.save(profile);
            log.debug("Added {} words learned for user: {}", count, userId);
        });
    }
}
