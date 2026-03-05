package com.v1no.LJL.learning_service.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.learning_service.model.dto.request.CreateCategoryRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateCategoryRequest;
import com.v1no.LJL.learning_service.model.dto.response.CategoryDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CategorySummaryResponse;
import com.v1no.LJL.learning_service.model.dto.response.LanguageCatalogResponse;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import com.v1no.LJL.learning_service.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create new category")
    public ResponseEntity<ApiResponse<CategorySummaryResponse>> create(
        @Valid @RequestBody CreateCategoryRequest request
    ) {
        log.info("POST /api/v1/categories - name={}", request.name());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(categoryService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<ApiResponse<CategorySummaryResponse>> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateCategoryRequest request
    ) {
        log.info("PUT /api/v1/categories/{}", id);
        return ResponseEntity.ok(ApiResponse.ok(categoryService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/categories/{}", id);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category summary by id")
    public ResponseEntity<ApiResponse<CategorySummaryResponse>> findById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.findById(id)));
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "Get category detail with lessons and sentences")
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> findDetailById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.findDetailById(id)));
    }

    @GetMapping
    @Operation(summary = "Get all active categories")
    public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.findAll()));
    }

    @GetMapping("/by-language")
    @Operation(summary = "Get all active categories by language")
    public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>> findByLanguage(
        @RequestParam LanguageCode languageCode
    ) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.findByLanguageCode(languageCode)));
    }
}