package com.v1no.LJL.community_service.mapper;
import com.v1no.LJL.community_service.model.dto.request.RatingCreateRequest;
import com.v1no.LJL.community_service.model.dto.request.RatingUpdateRequest;
import com.v1no.LJL.community_service.model.dto.response.RatingDetailResponse;
import com.v1no.LJL.community_service.model.dto.response.RatingStatsResponse;
import com.v1no.LJL.community_service.model.entity.Rating;
import com.v1no.LJL.community_service.model.enums.CommentTargetType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RatingMapper {
    public RatingDetailResponse toDetail(Rating rating) {
        if (rating == null) return null;

        return RatingDetailResponse.builder()
                .id(rating.getId())
                .userId(rating.getUserId())
                .targetType(rating.getTargetType())
                .score(rating.getScore())
                .review(rating.getReview())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }

    public List<RatingDetailResponse> toDetailList(List<Rating> ratings) {
        if (ratings == null) return Collections.emptyList();
        return ratings.stream()
                .map(this::toDetail)
                .collect(Collectors.toList());
    }

    public RatingStatsResponse toStats(
            CommentTargetType targetType,
            Double averageScore,
            Long totalRatings,
            Long fiveStars,
            Long fourStars,
            Long threeStars,
            Long twoStars,
            Long oneStar
    ) {
        return RatingStatsResponse.builder()
                .targetType(targetType)
                .averageScore(averageScore != null ? Math.round(averageScore * 10.0) / 10.0 : 0.0)
                .totalRatings(totalRatings != null ? totalRatings : 0L)
                .fiveStars(fiveStars != null ? fiveStars : 0L)
                .fourStars(fourStars != null ? fourStars : 0L)
                .threeStars(threeStars != null ? threeStars : 0L)
                .twoStars(twoStars != null ? twoStars : 0L)
                .oneStar(oneStar != null ? oneStar : 0L)
                .build();
    }

    public Rating toEntity(RatingCreateRequest request, UUID userId) {
        return Rating.builder()
                .userId(userId)
                .targetType(request.targetType())
                .score(request.score())
                .review(request.review())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateEntity(Rating rating, RatingUpdateRequest request) {
        rating.setScore(request.score());
        rating.setReview(request.review());
        rating.setUpdatedAt(LocalDateTime.now());
    }
}
