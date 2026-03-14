package com.v1no.LJL.learning_service.controller;

import com.v1no.LJL.learning_service.model.dto.request.CardDeckRequest;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckResponse;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import com.v1no.LJL.learning_service.service.CardDeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/{id}")
    public ResponseEntity<CardDeckDetailResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(cardDeckService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CardDeckResponse>> getAll(
            @RequestParam(required = false) LanguageCode language,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(cardDeckService.getAll(userId, language, pageable));
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