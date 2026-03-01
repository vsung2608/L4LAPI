package com.v1no.LJL.content_service.service;

import java.util.List;
import java.util.UUID;

import com.v1no.LJL.content_service.model.dto.request.CreateArticleCategoryRequest;
import com.v1no.LJL.content_service.model.dto.request.UpdateArticleCategoryRequest;
import com.v1no.LJL.content_service.model.dto.response.ArticleCategoryResponse;

public interface ArticleCategoryService {
    ArticleCategoryResponse create(CreateArticleCategoryRequest request);
    ArticleCategoryResponse update(UUID id, UpdateArticleCategoryRequest request);
    void delete(UUID id);
    ArticleCategoryResponse findById(UUID id);
    List<ArticleCategoryResponse> findAll();
}
