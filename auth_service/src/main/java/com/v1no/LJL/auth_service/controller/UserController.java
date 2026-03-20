package com.v1no.LJL.auth_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v1no.LJL.auth_service.model.dto.request.UpdateProfileRequest;
import com.v1no.LJL.auth_service.model.dto.request.UpdateSettingsRequest;
import com.v1no.LJL.auth_service.model.dto.response.UserProfileDetailResponse;
import com.v1no.LJL.auth_service.model.dto.response.UserProfileSummaryResponse;
import com.v1no.LJL.auth_service.model.dto.response.VipStatusResponse;
import com.v1no.LJL.auth_service.service.UserProfileSerivce;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileSerivce userProfileSerivce;

    @PutMapping("/internal/{userId}/upgrade-vip")
    void upgradeToVip(
        @PathVariable("userId") UUID userId,
        @RequestParam("planCode") String planCode,
        @RequestParam(value = "durationDays", required = false, defaultValue = "30") Integer durationDays
    ){
        userProfileSerivce.upgradeToVip(userId, planCode, durationDays);
    }

    @GetMapping("/summary")
    public ResponseEntity<UserProfileSummaryResponse> getProfileSummary(
        @RequestHeader("X-User-Id") UUID userId
    ) {
        UserProfileSummaryResponse summary = userProfileSerivce.getProfileSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/detail")
    public ResponseEntity<UserProfileDetailResponse> getProfileDetail(
        @RequestHeader("X-User-Id") UUID userId
    ) {
        UserProfileDetailResponse detail = userProfileSerivce.getProfileDetail(userId);
        return ResponseEntity.ok(detail);
    }

    @PutMapping
    public ResponseEntity<UserProfileDetailResponse> updateProfile(
        @RequestHeader("X-User-Id") UUID userId,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserProfileDetailResponse response = userProfileSerivce.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/settings")
    public ResponseEntity<UserProfileDetailResponse> updateSettings(
        @RequestHeader("X-User-Id") UUID userId,
        @Valid @RequestBody UpdateSettingsRequest request
    ) {
        UserProfileDetailResponse response = userProfileSerivce.updateSettings(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vip")
    public ResponseEntity<VipStatusResponse> getVipStatus(
        @RequestHeader("X-User-Id") UUID userId
    ) {
        VipStatusResponse status = userProfileSerivce.getVipStatus(userId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/vip/cancel")
    public ResponseEntity<VipStatusResponse> cancelVip(
        @RequestHeader("X-User-Id") UUID userId
    ) {
        VipStatusResponse status = userProfileSerivce.cancelVip(userId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/internal/{userId}/study-time")
    public ResponseEntity<Void> addStudyTime(
        @PathVariable UUID userId,
        @RequestParam int minutes
    ) {
        userProfileSerivce.addStudyTime(userId, minutes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/{userId}/lessons-completed")
    public ResponseEntity<Void> incrementLessonsCompleted(
        @PathVariable UUID userId
    ) {
        userProfileSerivce.incrementLessonsCompleted(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/{userId}/words-learned")
    public ResponseEntity<Void> addWordsLearned(
        @PathVariable UUID userId,
        @RequestParam int count
    ) {
        userProfileSerivce.addWordsLearned(userId, count);
        return ResponseEntity.ok().build();
    }
}
