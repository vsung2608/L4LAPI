package com.v1no.LJL.learning_service.controller;

import com.v1no.LJL.learning_service.model.dto.request.FlashCardRequest;
import com.v1no.LJL.learning_service.model.dto.response.FlashCardResponse;
import com.v1no.LJL.learning_service.service.FlashCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FlashCardController {

    private final FlashCardService flashCardService;

    @PostMapping("/decks/{deckId}/cards")
    public ResponseEntity<FlashCardResponse> create(
            @PathVariable UUID deckId,
            @Valid @ModelAttribute FlashCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(flashCardService.create(deckId, request));
    }

    @GetMapping("/decks/{deckId}/cards")
    public ResponseEntity<List<FlashCardResponse>> getAllByDeck(
            @PathVariable UUID deckId,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(flashCardService.getAllByDeck(userId, deckId));
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<FlashCardResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(flashCardService.getById(id));
    }

    @PutMapping("/cards/{id}")
    public ResponseEntity<FlashCardResponse> update(
            @PathVariable UUID id,
            @Valid @ModelAttribute FlashCardRequest request) {
        return ResponseEntity.ok(flashCardService.update(id, request));
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        flashCardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}