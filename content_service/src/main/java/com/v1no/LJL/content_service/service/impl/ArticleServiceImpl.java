package com.v1no.LJL.content_service.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.common.exception.BusinessException;
import com.v1no.LJL.common.exception.DuplicateResourceException;
import com.v1no.LJL.common.exception.ResourceNotFoundException;
import com.v1no.LJL.content_service.mapper.ArticleMapper;
import com.v1no.LJL.content_service.model.dto.request.CreateArticleRequest;
import com.v1no.LJL.content_service.model.dto.request.UpdateArticleRequest;
import com.v1no.LJL.content_service.model.dto.response.ArticleDetailResponse;
import com.v1no.LJL.content_service.model.dto.response.ArticleSummaryResponse;
import com.v1no.LJL.content_service.model.entity.Article;
import com.v1no.LJL.content_service.model.entity.ArticleCategory;
import com.v1no.LJL.content_service.model.enums.ArticleStatus;
import com.v1no.LJL.content_service.repository.ArticleCategoryRepository;
import com.v1no.LJL.content_service.repository.ArticleRepository;
import com.v1no.LJL.content_service.service.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleCategoryRepository categoryRepository;
    private final ArticleMapper articleMapper;

    @Override
    public ArticleDetailResponse create(CreateArticleRequest request) {
        log.info("Creating article: slug={}, authorId={}", request.slug(), request.authorId());

        if (articleRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("Slug already exists: " + request.slug());
        }

        ArticleCategory category = resolveCategory(request.categoryId());
        Article saved = articleRepository.save(articleMapper.toEntity(request, category));

        log.info("Article created: id={}", saved.getId());
        return articleMapper.toDetail(saved);
    }

    @Override
    public ArticleDetailResponse update(UUID id, UpdateArticleRequest request) {
        log.info("Updating article: id={}", id);

        Article article = findArticleById(id);

        if (articleRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new DuplicateResourceException("Slug already exists: " + request.slug());
        }

        ArticleCategory category = resolveCategory(request.categoryId());
        articleMapper.updateEntity(article, request, category);

        Article saved = articleRepository.save(article);
        return articleMapper.toDetail(saved);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting article: id={}", id);
        Article article = findArticleById(id);

        if (ArticleStatus.PUBLISHED.equals(article.getStatus())) {
            throw new BusinessException("Cannot delete a published article — archive it first");
        }

        articleRepository.delete(article);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDetailResponse findById(UUID id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + id));
        return articleMapper.toDetail(article);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDetailResponse findBySlug(String slug) {
        Article article = articleRepository.findBySlugWithCategory(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + slug));

        // Increment view count — fire and forget, không block response
        articleRepository.incrementViewCount(article.getId());

        return articleMapper.toDetail(article);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleSummaryResponse> findAllPublished(Pageable pageable) {
        return articleRepository.findAllPublished(pageable)
            .map(articleMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleSummaryResponse> findByCategoryId(UUID categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Article category not found: " + categoryId);
        }
        return articleRepository.findPublishedByCategoryId(categoryId, pageable)
            .map(articleMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleSummaryResponse> findByAuthorId(UUID authorId, Pageable pageable) {
        return articleRepository.findAllByAuthorId(authorId, pageable)
            .map(articleMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleSummaryResponse> findFeatured() {
        return articleRepository.findFeatured()
            .stream()
            .map(articleMapper::toSummary)
            .toList();
    }

    @Override
    public ArticleDetailResponse publish(UUID id) {
        log.info("Publishing article: id={}", id);
        Article article = findArticleById(id);
        article.publish();
        return articleMapper.toDetail(articleRepository.save(article));
    }

    @Override
    public ArticleDetailResponse archive(UUID id) {
        log.info("Archiving article: id={}", id);
        Article article = findArticleById(id);
        article.archive();
        return articleMapper.toDetail(articleRepository.save(article));
    }

    private Article findArticleById(UUID id) {
        return articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + id));
    }

    // category là optional — article có thể không thuộc category nào
    private ArticleCategory resolveCategory(UUID categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Article category not found: " + categoryId
            ));
    }
}
