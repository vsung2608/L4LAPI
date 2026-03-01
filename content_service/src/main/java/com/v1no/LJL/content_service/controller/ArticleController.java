package com.v1no.LJL.content_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.content_service.model.dto.request.CreateArticleRequest;
import com.v1no.LJL.content_service.model.dto.request.UpdateArticleRequest;
import com.v1no.LJL.content_service.model.dto.response.ArticleDetailResponse;
import com.v1no.LJL.content_service.model.dto.response.ArticleSummaryResponse;
import com.v1no.LJL.content_service.service.ArticleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Article", description = "Article management APIs")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    @Operation(summary = "Create article")
    public ResponseEntity<ApiResponse<ArticleDetailResponse>> create(
        @Valid @RequestBody CreateArticleRequest request
    ) {
        log.info("POST /api/v1/articles - slug={}", request.slug());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(articleService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update article")
    public ResponseEntity<ApiResponse<ArticleDetailResponse>> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateArticleRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(articleService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete article — chỉ xóa được DRAFT")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get article by id")
    public ResponseEntity<ApiResponse<ArticleDetailResponse>> findById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(articleService.findById(id)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get article by slug — tự động tăng viewCount")
    public ResponseEntity<ApiResponse<ArticleDetailResponse>> findBySlug(
        @PathVariable String slug
    ) {
        return ResponseEntity.ok(ApiResponse.ok(articleService.findBySlug(slug)));
    }

    @GetMapping
    @Operation(summary = "Get all published articles with pagination")
    public ResponseEntity<ApiResponse<Page<ArticleSummaryResponse>>> findAllPublished(
        @PageableDefault(size = 20, sort = "publishedAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(articleService.findAllPublished(pageable)));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get published articles by category")
    public ResponseEntity<ApiResponse<Page<ArticleSummaryResponse>>> findByCategoryId(
        @PathVariable UUID categoryId,
        @PageableDefault(size = 20, sort = "publishedAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            ApiResponse.ok(articleService.findByCategoryId(categoryId, pageable))
        );
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get articles by author")
    public ResponseEntity<ApiResponse<Page<ArticleSummaryResponse>>> findByAuthorId(
        @PathVariable UUID authorId,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            ApiResponse.ok(articleService.findByAuthorId(authorId, pageable))
        );
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured articles")
    public ResponseEntity<ApiResponse<List<ArticleSummaryResponse>>> findFeatured() {
        return ResponseEntity.ok(ApiResponse.ok(articleService.findFeatured()));
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish article")
    public ResponseEntity<ApiResponse<ArticleDetailResponse>> publish(@PathVariable UUID id) {
        log.info("PATCH /api/v1/articles/{}/publish", id);
        return ResponseEntity.ok(ApiResponse.ok(articleService.publish(id)));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive article")
    public ResponseEntity<ApiResponse<ArticleDetailResponse>> archive(@PathVariable UUID id) {
        log.info("PATCH /api/v1/articles/{}/archive", id);
        return ResponseEntity.ok(ApiResponse.ok(articleService.archive(id)));
    }
}
