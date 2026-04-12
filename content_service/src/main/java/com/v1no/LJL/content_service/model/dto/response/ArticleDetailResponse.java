package com.v1no.LJL.content_service.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.v1no.LJL.content_service.model.enums.ArticleStatus;

public record ArticleDetailResponse(
    UUID id,
    ArticleCategoryResponse category,
    String title,
    String slug,
    String excerpt,
    String content,
    String metaTitle,
    String metaDescription,
    List<String> metaKeywords,
    String featuredImage,
    String thumbnailImage,
    UUID authorId,
    ArticleStatus status,
    Integer estimatedReadMinutes,
    Integer viewCount,
    Integer likeCount,
    Integer commentCount,
    Boolean allowComments,
    LocalDateTime publishedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
