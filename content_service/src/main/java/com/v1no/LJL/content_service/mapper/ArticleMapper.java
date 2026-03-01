package com.v1no.LJL.content_service.mapper;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.v1no.LJL.content_service.model.dto.request.CreateArticleRequest;
import com.v1no.LJL.content_service.model.dto.request.UpdateArticleRequest;
import com.v1no.LJL.content_service.model.dto.response.ArticleDetailResponse;
import com.v1no.LJL.content_service.model.dto.response.ArticleSummaryResponse;
import com.v1no.LJL.content_service.model.entity.Article;
import com.v1no.LJL.content_service.model.entity.ArticleCategory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleMapper {

    private final ArticleCategoryMapper categoryMapper;

    public Article toEntity(CreateArticleRequest request, ArticleCategory category) {
        Article article = Article.builder()
            .category(category)
            .title(request.title())
            .slug(request.slug())
            .excerpt(request.excerpt())
            .content(request.content())
            .metaTitle(request.metaTitle())
            .metaDescription(request.metaDescription())
            .metaKeywords(
                request.metaKeywords() != null ? request.metaKeywords() : new ArrayList<>()
            )
            .featuredImage(request.featuredImage())
            .thumbnailImage(request.thumbnailImage())
            .authorId(request.authorId())
            .isFeatured(request.isFeatured() != null ? request.isFeatured() : false)
            .allowComments(request.allowComments() != null ? request.allowComments() : true)
            .build();

        article.recalculateReadTime();
        return article;
    }

    public void updateEntity(Article article, UpdateArticleRequest request, ArticleCategory category) {
        article.setCategory(category);
        article.setTitle(request.title());
        article.setSlug(request.slug());
        article.setExcerpt(request.excerpt());
        article.setContent(request.content());
        article.setMetaTitle(request.metaTitle());
        article.setMetaDescription(request.metaDescription());
        article.setMetaKeywords(
            request.metaKeywords() != null ? request.metaKeywords() : new ArrayList<>()
        );
        article.setFeaturedImage(request.featuredImage());
        article.setThumbnailImage(request.thumbnailImage());
        article.setIsFeatured(request.isFeatured() != null ? request.isFeatured() : false);
        article.setAllowComments(request.allowComments() != null ? request.allowComments() : true);
        article.recalculateReadTime();
    }

    public ArticleSummaryResponse toSummary(Article article) {
        return new ArticleSummaryResponse(
            article.getId(),
            article.getCategory() != null ? article.getCategory().getId() : null,
            article.getCategory() != null ? article.getCategory().getName() : null,
            article.getTitle(),
            article.getSlug(),
            article.getExcerpt(),
            article.getThumbnailImage(),
            article.getAuthorId().toString(),
            article.getStatus(),
            article.getEstimatedReadMinutes(),
            article.getViewCount(),
            article.getLikeCount(),
            article.getCommentCount(),
            article.getIsFeatured(),
            article.getPublishedAt(),
            article.getCreatedAt()
        );
    }

    public ArticleDetailResponse toDetail(Article article) {
        return new ArticleDetailResponse(
            article.getId(),
            article.getCategory() != null
                ? categoryMapper.toResponse(article.getCategory()) : null,
            article.getTitle(),
            article.getSlug(),
            article.getExcerpt(),
            article.getContent(),
            article.getMetaTitle(),
            article.getMetaDescription(),
            article.getMetaKeywords(),
            article.getFeaturedImage(),
            article.getThumbnailImage(),
            article.getAuthorId(),
            article.getStatus(),
            article.getEstimatedReadMinutes(),
            article.getViewCount(),
            article.getLikeCount(),
            article.getCommentCount(),
            article.getIsFeatured(),
            article.getAllowComments(),
            article.getPublishedAt(),
            article.getCreatedAt(),
            article.getUpdatedAt()
        );
    }
}
