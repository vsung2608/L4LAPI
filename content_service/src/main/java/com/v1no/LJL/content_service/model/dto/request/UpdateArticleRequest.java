package com.v1no.LJL.content_service.model.dto.request;

import java.util.List;
import java.util.UUID;

import com.v1no.LJL.content_service.model.enums.ArticleStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateArticleRequest(
    UUID categoryId,

    @NotBlank @Size(max = 300) String title,
    @NotBlank @Size(max = 300) String slug,

    String excerpt,

    @NotBlank String content,

    @Size(max = 200) String metaTitle,
    @Size(max = 160) String metaDescription,
    List<String> metaKeywords,

    String featuredImage,
    String thumbnailImage,

    @NotNull ArticleStatus status,

    Boolean isFeatured,
    Boolean allowComments
) {}
