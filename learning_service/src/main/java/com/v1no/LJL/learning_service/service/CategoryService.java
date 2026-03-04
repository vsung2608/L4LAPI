package com.v1no.LJL.learning_service.service;

import java.util.List;
import java.util.UUID;

import com.v1no.LJL.learning_service.model.dto.request.CreateCategoryRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateCategoryRequest;
import com.v1no.LJL.learning_service.model.dto.response.CategoryDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CategorySummaryResponse;

public interface CategoryService {

    CategorySummaryResponse create(CreateCategoryRequest request);

    CategorySummaryResponse update(UUID id, UpdateCategoryRequest request);

    void delete(UUID id);

    CategorySummaryResponse findById(UUID id);

    List<CategorySummaryResponse> findByLanguageCode(String languageCode);

    CategoryDetailResponse findDetailById(UUID id);

    List<CategorySummaryResponse> findAll();
}