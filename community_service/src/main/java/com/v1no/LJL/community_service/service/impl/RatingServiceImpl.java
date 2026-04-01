package com.v1no.LJL.community_service.service.impl;

import com.v1no.LJL.community_service.mapper.RatingMapper;
import com.v1no.LJL.community_service.model.dto.request.RatingCreateRequest;
import com.v1no.LJL.community_service.model.dto.request.RatingUpdateRequest;
import com.v1no.LJL.community_service.model.dto.response.RatingDetailResponse;
import com.v1no.LJL.community_service.model.dto.response.RatingStatsResponse;
import com.v1no.LJL.community_service.model.entity.Rating;
import com.v1no.LJL.community_service.model.enums.CommentTargetType;
import com.v1no.LJL.community_service.repository.RatingRepository;
import com.v1no.LJL.community_service.service.RatingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Override
    @Transactional
    public RatingDetailResponse createRating(RatingCreateRequest request, UUID userId) {
        boolean alreadyRated = ratingRepository.existsByUserIdAndTargetType(
                userId, request.targetType());

        if (alreadyRated) {
            throw new IllegalStateException(
                    "User [" + userId + "] already rated");
        }

        Rating saved = ratingRepository.save(ratingMapper.toEntity(request, userId));
        log.info("Created rating [id={}] score=[{}] for target [{}] by user [id={}]",
                saved.getId(), saved.getScore(), request.targetType(), userId);
        return ratingMapper.toDetail(saved);
    }

    @Override
    @Transactional
    public RatingDetailResponse updateRating(UUID ratingId, RatingUpdateRequest request, UUID userId) {
        Rating rating = getRatingOrThrow(ratingId);
        checkOwnership(rating, userId);

        ratingMapper.updateEntity(rating, request);
        Rating updated = ratingRepository.save(rating);
        log.info("Updated rating [id={}] to score=[{}] by user [id={}]", ratingId, request.score(), userId);
        return ratingMapper.toDetail(updated);
    }

    @Override
    @Transactional
    public void deleteRating(UUID ratingId, UUID userId) {
        Rating rating = getRatingOrThrow(ratingId);
        checkOwnership(rating, userId);

        ratingRepository.delete(rating);
        log.info("Deleted rating [id={}] by user [id={}]", ratingId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingDetailResponse getRatingByUserAndTarget(
            UUID userId, CommentTargetType targetType) {
        Rating rating = ratingRepository.findByUserIdAndTargetType(userId, targetType)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rating not found for user [" + userId + "] target [" + targetType + "]"));
        return ratingMapper.toDetail(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RatingDetailResponse> getRatingsByTarget(
            CommentTargetType targetType, Pageable pageable) {
        return ratingRepository.findByTargetType(targetType, pageable)
                .map(ratingMapper::toDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RatingDetailResponse> getRatingsByUser(UUID userId, Pageable pageable) {
        return ratingRepository.findByUserId(userId, pageable)
                .map(ratingMapper::toDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingStatsResponse getRatingStats(CommentTargetType targetType) {
        Double avg    = ratingRepository.findAverageScoreByTarget(targetType);
        long total    = ratingRepository.countByTargetType(targetType);
        long five     = ratingRepository.countByTargetTypeAndScore(targetType, 5);
        long four     = ratingRepository.countByTargetTypeAndScore(targetType, 4);
        long three    = ratingRepository.countByTargetTypeAndScore(targetType, 3);
        long two      = ratingRepository.countByTargetTypeAndScore(targetType, 2);
        long one      = ratingRepository.countByTargetTypeAndScore(targetType, 1);

        return ratingMapper.toStats(targetType, avg, total, five, four, three, two, one);
    }

    private Rating getRatingOrThrow(UUID id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rating not found: " + id));
    }

    private void checkOwnership(Rating rating, UUID userId) {
        if (!rating.getUserId().equals(userId)) {
            throw new SecurityException("User [" + userId + "] is not allowed to modify rating [" + rating.getId() + "]");
        }
    }
}