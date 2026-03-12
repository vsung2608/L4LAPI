package com.v1no.LJL.learning_service.controller;

import com.v1no.LJL.learning_service.model.dto.request.FlashCardRequest;
import com.v1no.LJL.learning_service.model.dto.response.FlashCardResponse;
import com.v1no.LJL.learning_service.service.FlashCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FlashCardController {

    private final FlashCardService flashCardService;

    // Nested resource: /decks/{deckId}/cards
    @PostMapping("/decks/{deckId}/cards")
    public ResponseEntity<FlashCardResponse> create(
            @PathVariable Long deckId,
            @Valid @RequestBody FlashCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(flashCardService.create(deckId, request));
    }

    @GetMapping("/decks/{deckId}/cards")
    public ResponseEntity<Page<FlashCardResponse>> getAllByDeck(
            @PathVariable Long deckId,
            @PageableDefault(size = 20, sort = "displayOrder") Pageable pageable) {
        return ResponseEntity.ok(flashCardService.getAllByDeck(deckId, pageable));
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<FlashCardResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(flashCardService.getById(id));
    }

    @PutMapping("/cards/{id}")
    public ResponseEntity<FlashCardResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FlashCardRequest request) {
        return ResponseEntity.ok(flashCardService.update(id, request));
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flashCardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}