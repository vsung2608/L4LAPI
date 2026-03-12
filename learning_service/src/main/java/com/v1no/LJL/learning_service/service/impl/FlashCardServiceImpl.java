// FlashCardServiceImpl.java
package com.v1no.LJL.learning_service.service.impl;

import com.v1no.LJL.common.exception.ResourceNotFoundException;
import com.v1no.LJL.learning_service.mapper.FlashCardMapper;
import com.v1no.LJL.learning_service.model.dto.request.FlashCardRequest;
import com.v1no.LJL.learning_service.model.dto.response.FlashCardResponse;
import com.v1no.LJL.learning_service.model.entity.CardDeck;
import com.v1no.LJL.learning_service.model.entity.FlashCard;
import com.v1no.LJL.learning_service.repository.CardDeckRepository;
import com.v1no.LJL.learning_service.repository.FlashCardRepository;
import com.v1no.LJL.learning_service.service.FlashCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlashCardServiceImpl implements FlashCardService {

    private final FlashCardRepository flashCardRepository;
    private final CardDeckRepository cardDeckRepository;
    private final FlashCardMapper flashCardMapper;

    @Override
    @Transactional
    public FlashCardResponse create(Long deckId, FlashCardRequest request) {
        log.info("Creating FlashCard in deck: {}", deckId);
        CardDeck deck = cardDeckRepository.findById(deckId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find flashcard with ID::%s".formatted(deckId)));
        FlashCard card = flashCardMapper.toEntity(request, deck);
        FlashCard saved = flashCardRepository.save(card);
        log.info("FlashCard created with id: {}", saved.getId());
        return flashCardMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FlashCardResponse getById(Long id) {
        log.debug("Fetching FlashCard with id: {}", id);
        FlashCard card = flashCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find flashcard with ID::%s".formatted(id)));
        return flashCardMapper.toResponse(card);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlashCardResponse> getAllByDeck(Long deckId, Pageable pageable) {
        log.debug("Fetching FlashCards for deck: {}", deckId);
        if (!cardDeckRepository.existsById(deckId)) {
            throw new ResourceNotFoundException("Cannot find card deck with ID::%s".formatted(deckId));
        }
        return flashCardRepository.findAllByDeckId(deckId, pageable)
                .map(flashCardMapper::toResponse);
    }

    @Override
    @Transactional
    public FlashCardResponse update(Long id, FlashCardRequest request) {
        log.info("Updating FlashCard with id: {}", id);
        FlashCard card = flashCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find flashcard with ID::%s".formatted(id)));
        flashCardMapper.updateEntity(card, request);
        return flashCardMapper.toResponse(card);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting FlashCard with id: {}", id);
        if (!flashCardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot find flashcard with ID::%s".formatted(id));
        }
        flashCardRepository.deleteById(id);
    }
}