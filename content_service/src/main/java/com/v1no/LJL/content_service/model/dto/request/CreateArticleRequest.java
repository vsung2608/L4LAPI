package com.v1no.LJL.content_service.model.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateArticleRequest(
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

    @NotNull UUID authorId,

    Boolean isFeatured,
    Boolean allowComments
) {}
