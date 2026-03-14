package com.v1no.LJL.learning_service.service;

import com.v1no.LJL.learning_service.model.dto.request.FlashCardRequest;
import com.v1no.LJL.learning_service.model.dto.response.FlashCardResponse;

import java.util.List;
import java.util.UUID;


public interface FlashCardService {

    FlashCardResponse create(UUID deckId, FlashCardRequest request);
    FlashCardResponse getById(UUID id);
    List<FlashCardResponse> getAllByDeck(UUID userId, UUID deckId);
    FlashCardResponse update(UUID id, FlashCardRequest request);
    void delete(UUID id);
}