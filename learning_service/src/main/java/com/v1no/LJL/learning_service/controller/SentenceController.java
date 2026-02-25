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
import org.springframework.web.bind.annotation.RestController;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.learning_service.model.dto.request.CreateSentenceRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateSentenceRequest;
import com.v1no.LJL.learning_service.model.dto.response.SentenceResponse;
import com.v1no.LJL.learning_service.service.SentenceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/sentences")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sentence", description = "Sentence management APIs")
public class SentenceController {

    private final SentenceService sentenceService;

    @PostMapping
    @Operation(summary = "Create new sentence")
    public ResponseEntity<ApiResponse<SentenceResponse>> create(
        @Valid @RequestBody CreateSentenceRequest request
    ) {
        log.info("POST /api/v1/sentences - lessonId={}, orderIndex={}", request.lessonId(), request.orderIndex());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(sentenceService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sentence")
    public ResponseEntity<ApiResponse<SentenceResponse>> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateSentenceRequest request
    ) {
        log.info("PUT /api/v1/sentences/{}", id);
        return ResponseEntity.ok(ApiResponse.ok(sentenceService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sentence")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/sentences/{}", id);
        sentenceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sentence by id")
    public ResponseEntity<ApiResponse<SentenceResponse>> findById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(sentenceService.findById(id)));
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Get all sentences by lesson, ordered by orderIndex")
    public ResponseEntity<ApiResponse<List<SentenceResponse>>> findByLessonId(
        @PathVariable UUID lessonId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(sentenceService.findByLessonId(lessonId)));
    }
}
