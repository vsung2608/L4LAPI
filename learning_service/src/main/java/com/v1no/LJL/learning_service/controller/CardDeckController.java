package com.v1no.LJL.learning_service.controller;

import com.v1no.LJL.common.dto.PageResponse;
import com.v1no.LJL.learning_service.model.dto.request.CardDeckRequest;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckResponse;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import com.v1no.LJL.learning_service.service.CardDeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/decks")
@RequiredArgsConstructor
public class CardDeckController {

    private final CardDeckService cardDeckService;

    @PostMapping
    public ResponseEntity<CardDeckResponse> create(@Valid @ModelAttribute CardDeckRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardDeckService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CardDeckResponse>> getAll(
            @RequestParam(required = false) LanguageCode language,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(cardDeckService.getAll(userId, language));
    }

    @GetMapping("/admin")
    public ResponseEntity<PageResponse<CardDeckResponse>> getAllForAdmin(
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "en") String language) {
        return ResponseEntity.ok(cardDeckService.getAllForAdmin(page, size, language));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDeckResponse> update(
            @PathVariable UUID id,
            @Valid @ModelAttribute CardDeckRequest request) {
        return ResponseEntity.ok(cardDeckService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cardDeckService.delete(id);
        return ResponseEntity.noContent().build();
    }
}