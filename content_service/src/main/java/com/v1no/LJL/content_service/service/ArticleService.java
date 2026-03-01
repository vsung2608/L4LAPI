package com.v1no.LJL.content_service.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v1no.LJL.content_service.model.dto.request.CreateArticleRequest;
import com.v1no.LJL.content_service.model.dto.request.UpdateArticleRequest;
import com.v1no.LJL.content_service.model.dto.response.ArticleDetailResponse;
import com.v1no.LJL.content_service.model.dto.response.ArticleSummaryResponse;

public interface ArticleService {
    ArticleDetailResponse create(CreateArticleRequest request);
    ArticleDetailResponse update(UUID id, UpdateArticleRequest request);
    void delete(UUID id);
    ArticleDetailResponse findById(UUID id);
    ArticleDetailResponse findBySlug(String slug);
    Page<ArticleSummaryResponse> findAllPublished(Pageable pageable);
    Page<ArticleSummaryResponse> findByCategoryId(UUID categoryId, Pageable pageable);
    Page<ArticleSummaryResponse> findByAuthorId(UUID authorId, Pageable pageable);
    List<ArticleSummaryResponse> findFeatured();
    ArticleDetailResponse publish(UUID id);
    ArticleDetailResponse archive(UUID id);
}