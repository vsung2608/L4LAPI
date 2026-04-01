package com.v1no.LJL.community_service.model.dto.response;


import com.v1no.LJL.community_service.model.enums.CommentTargetType;

import lombok.Builder;

@Builder
public record RatingStatsResponse(
        CommentTargetType targetType,   
        Double averageScore,
        Long totalRatings,
        Long fiveStars,
        Long fourStars,
        Long threeStars,
        Long twoStars,
        Long oneStar
) {
    
}
