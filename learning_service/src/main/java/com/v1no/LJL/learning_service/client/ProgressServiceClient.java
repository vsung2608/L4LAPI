package com.v1no.LJL.learning_service.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.common.dto.DeckProgressResponse;
import com.v1no.LJL.common.dto.DeckStudiedResponse;
import com.v1no.LJL.common.dto.LessonProgressSummary;
import com.v1no.LJL.learning_service.client.fallback.ProgressServiceClientFallback;

@FeignClient(
    name = "progressServiceClient",
    url = "${progress-service.base-url}",
    fallback = ProgressServiceClientFallback.class
)
public interface ProgressServiceClient {

    @GetMapping("/api/v1/progress/users/{userId}/lessons")
    ApiResponse<List<LessonProgressSummary>> getProgressByLessonIds(
        @PathVariable UUID userId,
        @RequestParam List<UUID> lessonIds
    );

    @GetMapping("/api/v1/progress/decks/{userId}/{deckId}")
    public ApiResponse<DeckProgressResponse> getDeckProgress(
            @PathVariable UUID userId,
            @PathVariable UUID deckId);

    @PostMapping("/api/v1/progress/users/{userId}/decks")
    public ApiResponse<List<DeckStudiedResponse>> getStudiedDecks(
            @PathVariable UUID userId,
            @RequestBody List<UUID> deckIds);
}
