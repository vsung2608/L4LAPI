package com.v1no.LJL.community_service.service;

import com.v1no.LJL.community_service.model.dto.request.RatingCreateRequest;
import com.v1no.LJL.community_service.model.dto.request.RatingUpdateRequest;
import com.v1no.LJL.community_service.model.dto.response.RatingDetailResponse;
import com.v1no.LJL.community_service.model.dto.response.RatingStatsResponse;
import com.v1no.LJL.community_service.model.enums.CommentTargetType;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RatingService {

    RatingDetailResponse createRating(RatingCreateRequest request, UUID userId);

    RatingDetailResponse updateRating(UUID ratingId, RatingUpdateRequest request, UUID userId);

    void deleteRating(UUID ratingId, UUID userId);

    RatingDetailResponse getRatingByUserAndTarget(UUID userId, CommentTargetType targetType);

    Page<RatingDetailResponse> getRatingsByTarget(
            CommentTargetType targetType,
            Pageable pageable
    );

    Page<RatingDetailResponse> getRatingsByUser(UUID userId, Pageable pageable);

    RatingStatsResponse getRatingStats(CommentTargetType targetType);
}