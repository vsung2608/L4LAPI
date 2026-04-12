package com.v1no.LJL.content_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.v1no.LJL.content_service.model.enums.ArticleStatus;

public record ArticleSummaryResponse(
    UUID id,
    UUID categoryId,
    String categoryName,
    String title,
    String slug,
    String excerpt,
    String thumbnailImage,
    String authorId,
    ArticleStatus status,
    Integer estimatedReadMinutes,
    Integer viewCount,
    Integer likeCount,
    Integer commentCount,
    LocalDateTime publishedAt,
    LocalDateTime createdAt
) {}
