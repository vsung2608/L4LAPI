package com.v1no.LJL.content_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.content_service.model.dto.request.CreateArticleCategoryRequest;
import com.v1no.LJL.content_service.model.dto.request.UpdateArticleCategoryRequest;
import com.v1no.LJL.content_service.model.dto.response.ArticleCategoryResponse;
import com.v1no.LJL.content_service.service.ArticleCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/article-categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ArticleCategory", description = "Article category management APIs")
public class ArticleCategoryController {

    private final ArticleCategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create article category")
    public ResponseEntity<ApiResponse<ArticleCategoryResponse>> create(
        @Valid @RequestBody CreateArticleCategoryRequest request
    ) {
        log.info("POST /api/v1/article-categories - slug={}", request.slug());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(categoryService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update article category")
    public ResponseEntity<ApiResponse<ArticleCategoryResponse>> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateArticleCategoryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete article category")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get article category by id")
    public ResponseEntity<ApiResponse<ArticleCategoryResponse>> findById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.findById(id)));
    }

    @GetMapping
    @Operation(summary = "Get all active article categories")
    public ResponseEntity<ApiResponse<List<ArticleCategoryResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.findAll()));
    }
}
