package com.v1no.LJL.learning_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
import com.v1no.LJL.common.dto.PageResponse;
import com.v1no.LJL.learning_service.model.dto.request.CreateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.response.LessonDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonPreviewResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonSummaryResponse;
import com.v1no.LJL.learning_service.model.enums.JlptLevel;
import com.v1no.LJL.learning_service.service.LessonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Lesson", description = "Lesson management APIs")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @Operation(summary = "Create new lesson")
    public ResponseEntity<ApiResponse<LessonSummaryResponse>> create(
        @Valid @RequestBody CreateLessonRequest request
    ) {
        log.info("POST /api/v1/lessons - categoryId={}", request.categoryId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(lessonService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update lesson")
    public ResponseEntity<ApiResponse<LessonSummaryResponse>> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateLessonRequest request
    ) {
        log.info("PUT /api/v1/lessons/{}", id);
        return ResponseEntity.ok(ApiResponse.ok(lessonService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete lesson")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/lessons/{}", id);
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lesson summary by id")
    public ResponseEntity<ApiResponse<LessonSummaryResponse>> findById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(lessonService.findById(id)));
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "Get lesson detail with sentences — dùng cho màn học")
    public ResponseEntity<ApiResponse<LessonDetailResponse>> findDetailById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(lessonService.findDetailById(id)));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get all active lessons by category")
    public ResponseEntity<ApiResponse<List<LessonPreviewResponse>>> findByCategoryId(
        @PathVariable UUID categoryId,
        @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(lessonService.findByCategoryId(categoryId, userId)));
    }

    @GetMapping("/level/{level}")
    @Operation(summary = "Get all active lessons by JLPT level")
    public ResponseEntity<ApiResponse<List<LessonPreviewResponse>>> findByLevel(
        @PathVariable JlptLevel level
    ) {
        return ResponseEntity.ok(ApiResponse.ok(lessonService.findByLevel(level)));
    }

    @GetMapping
    @Operation(summary = "Get all active lessons with pagination")
    public ResponseEntity<ApiResponse<PageResponse<LessonPreviewResponse>>> findAll(
        @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(lessonService.findAll(pageable)));
    }
}