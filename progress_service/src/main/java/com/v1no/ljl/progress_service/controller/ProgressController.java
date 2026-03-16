package com.v1no.ljl.progress_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.common.dto.DeckProgressResponse;
import com.v1no.LJL.common.dto.DeckStudiedResponse;
import com.v1no.LJL.common.dto.LessonProgressSummary;
import com.v1no.ljl.progress_service.model.dto.request.StartLessonRequest;
import com.v1no.ljl.progress_service.model.dto.request.StudyRecordRequest;
import com.v1no.ljl.progress_service.model.dto.request.UpdateProgressRequest;
import com.v1no.ljl.progress_service.model.dto.response.LessonProgressResponse;
import com.v1no.ljl.progress_service.model.dto.response.StudyRecordResponse;
import com.v1no.ljl.progress_service.model.enums.LearningMode;
import com.v1no.ljl.progress_service.model.enums.LessonStatus;
import com.v1no.ljl.progress_service.service.ProgressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Progress", description = "Learning progress APIs")
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/lessons/start")
    @Operation(summary = "Bắt đầu học một lesson — idempotent")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> startLesson(
        @Valid @RequestBody StartLessonRequest request
    ) {
        log.info("POST /api/v1/progress/lessons/start - userId={}, lessonId={}",
            request.userId(), request.lessonId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(progressService.startLesson(request)));
    }

    @PatchMapping("/lessons")
    @Operation(summary = "Cập nhật vị trí câu hiện tại và trạng thái bài học")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> updateProgress(
        @RequestParam UUID userId,
        @RequestParam UUID lessonId,
        @RequestParam LearningMode mode, 
        @Valid @RequestBody UpdateProgressRequest request
    ) {
        log.info("PATCH /api/v1/progress/lessons - userId={}, lessonId={}, mode={}", userId, lessonId, mode);
        return ResponseEntity.ok(
            ApiResponse.ok(progressService.updateProgress(userId, lessonId, mode, request))
        );
    }

    @GetMapping("/lessons")
    @Operation(summary = "Lấy progress của user cho một lesson cụ thể")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> getProgress(
        @RequestParam UUID userId,
        @RequestParam UUID lessonId
    ) {
        return ResponseEntity.ok(
            ApiResponse.ok(progressService.getProgress(userId, lessonId))
        );
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Lấy toàn bộ progress của user")
    public ResponseEntity<ApiResponse<List<LessonProgressResponse>>> getUserProgress(
        @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
            ApiResponse.ok(progressService.getUserProgress(userId))
        );
    }

    @GetMapping("/users/{userId}/status/{status}")
    @Operation(summary = "Lấy progress theo status — IN_PROGRESS hoặc COMPLETED")
    public ResponseEntity<ApiResponse<List<LessonProgressResponse>>> getUserProgressByStatus(
        @PathVariable UUID userId,
        @PathVariable LessonStatus status
    ) {
        return ResponseEntity.ok(
            ApiResponse.ok(progressService.getUserProgressByStatus(userId, status))
        );
    }

    @GetMapping("/users/{userId}/lessons")
    @Operation(summary = "Bulk get progress theo danh sách lessonId")
    public ResponseEntity<ApiResponse<List<LessonProgressSummary>>> getProgressByLessonIds(
        @PathVariable UUID userId,
        @RequestParam List<UUID> lessonIds
    ) {
        return ResponseEntity.ok(
            ApiResponse.ok(progressService.getProgressByLessonIds(userId, lessonIds))
        );
    }

    @PostMapping("/decks/{deckId}/cards/{cardId}/record")
    public ResponseEntity<StudyRecordResponse> recordCard(
            @PathVariable UUID deckId,
            @PathVariable UUID cardId,
            @Valid @RequestBody StudyRecordRequest request,
            @RequestHeader("X-User_id") UUID userId) {

        return ResponseEntity.ok(
                progressService.recordCard(userId, deckId, cardId, request)
        );
    }

    @GetMapping("/decks/{userId}/{deckId}")
    public ResponseEntity<ApiResponse<DeckProgressResponse>> getDeckProgress(
            @PathVariable UUID userId,
            @PathVariable UUID deckId) {

        return ResponseEntity.ok(
                ApiResponse.ok(progressService.getDeckProgress(userId, deckId))
        );
    }

    @PostMapping("/users/{userId}/decks")
    public ResponseEntity<ApiResponse<List<DeckStudiedResponse>>> getStudiedDecks(
            @PathVariable UUID userId,
            @RequestBody List<UUID> deckIds) {

        return ResponseEntity.ok(
                ApiResponse.ok(progressService.getStudiedDecks(userId, deckIds))
        );
    }
}