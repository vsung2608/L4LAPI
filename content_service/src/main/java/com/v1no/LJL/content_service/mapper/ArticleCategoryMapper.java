package com.v1no.LJL.content_service.mapper;

import org.springframework.stereotype.Component;

import com.v1no.LJL.content_service.model.dto.request.CreateArticleCategoryRequest;
import com.v1no.LJL.content_service.model.dto.request.UpdateArticleCategoryRequest;
import com.v1no.LJL.content_service.model.dto.response.ArticleCategoryResponse;
import com.v1no.LJL.content_service.model.entity.ArticleCategory;

@Component
public class ArticleCategoryMapper {

    public ArticleCategory toEntity(CreateArticleCategoryRequest request) {
        return ArticleCategory.builder()
            .name(request.name())
            .slug(request.slug())
            .description(request.description())
            .thumbnailUrl(request.thumbnailUrl())
            .displayOrder(request.displayOrder())
            .build();
    }

    public void updateEntity(ArticleCategory category, UpdateArticleCategoryRequest request) {
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
        category.setThumbnailUrl(request.thumbnailUrl());
        category.setDisplayOrder(request.displayOrder());
    }

    public ArticleCategoryResponse toResponse(ArticleCategory category) {
        return new ArticleCategoryResponse(
            category.getId(),
            category.getName(),
            category.getSlug(),
            category.getDescription(),
            category.getThumbnailUrl(),
            category.getDisplayOrder(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
}
