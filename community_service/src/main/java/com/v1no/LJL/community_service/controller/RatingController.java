package com.v1no.LJL.community_service.controller;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.community_service.model.dto.request.RatingCreateRequest;
import com.v1no.LJL.community_service.model.dto.request.RatingUpdateRequest;
import com.v1no.LJL.community_service.model.dto.response.RatingDetailResponse;
import com.v1no.LJL.community_service.model.dto.response.RatingStatsResponse;
import com.v1no.LJL.community_service.model.enums.CommentTargetType;
import com.v1no.LJL.community_service.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    @PostMapping
    public ResponseEntity<ApiResponse<RatingDetailResponse>> createRating(
            @Valid @RequestBody RatingCreateRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        RatingDetailResponse created = ratingService.createRating(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RatingDetailResponse>> updateRating(
            @PathVariable UUID id,
            @Valid @RequestBody RatingUpdateRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        RatingDetailResponse updated = ratingService.updateRating(id, request, userId);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRating(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        ratingService.deleteRating(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<RatingDetailResponse>> getMyRating(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam CommentTargetType targetType
    ) {
        RatingDetailResponse rating = ratingService.getRatingByUserAndTarget(userId, targetType);
        return ResponseEntity.ok(ApiResponse.ok(rating));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RatingDetailResponse>>> getRatingsByTarget(
            @RequestParam CommentTargetType targetType,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RatingDetailResponse> page = ratingService.getRatingsByTarget(targetType, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<RatingStatsResponse>> getRatingStats(
            @RequestParam CommentTargetType targetType
    ) {
        RatingStatsResponse stats = ratingService.getRatingStats(targetType);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<RatingDetailResponse>>> getMyRatings(
            @RequestHeader("X-User-Id") UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RatingDetailResponse> page = ratingService.getRatingsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }
}