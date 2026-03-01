package com.v1no.LJL.content_service.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.common.exception.BusinessException;
import com.v1no.LJL.common.exception.DuplicateResourceException;
import com.v1no.LJL.common.exception.ResourceNotFoundException;
import com.v1no.LJL.content_service.mapper.ArticleCategoryMapper;
import com.v1no.LJL.content_service.model.dto.request.CreateArticleCategoryRequest;
import com.v1no.LJL.content_service.model.dto.request.UpdateArticleCategoryRequest;
import com.v1no.LJL.content_service.model.dto.response.ArticleCategoryResponse;
import com.v1no.LJL.content_service.model.entity.ArticleCategory;
import com.v1no.LJL.content_service.repository.ArticleCategoryRepository;
import com.v1no.LJL.content_service.service.ArticleCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    private final ArticleCategoryRepository categoryRepository;
    private final ArticleCategoryMapper categoryMapper;

    @Override
    public ArticleCategoryResponse create(CreateArticleCategoryRequest request) {
        log.info("Creating article category: slug={}", request.slug());

        if (categoryRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("Slug already exists: " + request.slug());
        }

        ArticleCategory saved = categoryRepository.save(categoryMapper.toEntity(request));
        log.info("Article category created: id={}", saved.getId());
        return categoryMapper.toResponse(saved);
    }

    @Override
    public ArticleCategoryResponse update(UUID id, UpdateArticleCategoryRequest request) {
        log.info("Updating article category: id={}", id);

        ArticleCategory category = findCategoryById(id);

        if (categoryRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new DuplicateResourceException("Slug already exists: " + request.slug());
        }

        categoryMapper.updateEntity(category, request);
        ArticleCategory saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting article category: id={}", id);
        ArticleCategory category = findCategoryById(id);

        if (!category.getArticles().isEmpty()) {
            throw new BusinessException("Cannot delete category that still has articles");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleCategoryResponse findById(UUID id) {
        return categoryMapper.toResponse(findCategoryById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleCategoryResponse> findAll() {
        return categoryRepository.findAllOrderByDisplayOrderAsc()
            .stream()
            .map(categoryMapper::toResponse)
            .toList();
    }

    private ArticleCategory findCategoryById(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Article category not found: " + id));
    }
}
