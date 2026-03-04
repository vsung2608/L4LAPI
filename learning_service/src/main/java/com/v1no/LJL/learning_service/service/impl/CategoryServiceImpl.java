package com.v1no.LJL.learning_service.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.learning_service.exception.BusinessException;
import com.v1no.LJL.learning_service.exception.DuplicateResourceException;
import com.v1no.LJL.learning_service.exception.ResourceNotFoundException;
import com.v1no.LJL.learning_service.mapper.CategoryMapper;
import com.v1no.LJL.learning_service.model.dto.request.CreateCategoryRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateCategoryRequest;
import com.v1no.LJL.learning_service.model.dto.response.CategoryDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CategorySummaryResponse;
import com.v1no.LJL.learning_service.model.entity.Category;
import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.repository.CategoryRepository;
import com.v1no.LJL.learning_service.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategorySummaryResponse create(CreateCategoryRequest request) {
        log.info("Creating category: name={}", request.name());

        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Category already exists: " + request.name());
        }

        Category saved = categoryRepository.save(categoryMapper.toEntity(request));
        log.info("Category created: id={}", saved.getId());
        return categoryMapper.toSummary(saved);
    }

    @Override
    public CategorySummaryResponse update(UUID id, UpdateCategoryRequest request) {
        log.info("Updating category: id={}", id);

        Category category = findCategoryById(id);

        boolean nameChanged = !category.getName().equals(request.name());
        if (nameChanged && categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Category name already taken: " + request.name());
        }

        categoryMapper.updateEntity(category, request);
        Category saved = categoryRepository.save(category);
        log.info("Category updated: id={}", saved.getId());
        return categoryMapper.toSummary(saved);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting category: id={}", id);

        Category category = findCategoryById(id);

        if (!category.getLessons().isEmpty()) {
            throw new BusinessException("Cannot delete category that still has lessons");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategorySummaryResponse findById(UUID id) {
        return categoryMapper.toSummary(findCategoryById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDetailResponse findDetailById(UUID id) {
        Category category = categoryRepository.findByIdWithLessonsAndSentences(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        return categoryMapper.toDetail(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> findAll() {
        return categoryRepository.findAllByStatus(ContentStatus.ACTIVE)
            .stream()
            .map(categoryMapper::toSummary)
            .toList();
    }

    private Category findCategoryById(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> findByLanguageCode(String languageCode) {
        return categoryRepository.findAllByLanguageCode(languageCode)
            .stream()
            .map(categoryMapper::toSummary)
            .toList();
    }
}
