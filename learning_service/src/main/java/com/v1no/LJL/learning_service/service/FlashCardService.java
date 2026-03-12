package com.v1no.LJL.learning_service.service;

import com.v1no.LJL.learning_service.model.dto.request.FlashCardRequest;
import com.v1no.LJL.learning_service.model.dto.response.FlashCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlashCardService {

    FlashCardResponse create(Long deckId, FlashCardRequest request);
    FlashCardResponse getById(Long id);
    Page<FlashCardResponse> getAllByDeck(Long deckId, Pageable pageable);
    FlashCardResponse update(Long id, FlashCardRequest request);
    void delete(Long id);
}