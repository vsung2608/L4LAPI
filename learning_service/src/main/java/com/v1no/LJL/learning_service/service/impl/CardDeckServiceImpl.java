package com.v1no.LJL.learning_service.service.impl;

import com.v1no.LJL.common.exception.ResourceNotFoundException;
import com.v1no.LJL.learning_service.mapper.CardDeckMapper;
import com.v1no.LJL.learning_service.model.dto.request.CardDeckRequest;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckResponse;
import com.v1no.LJL.learning_service.model.entity.CardDeck;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import com.v1no.LJL.learning_service.repository.CardDeckRepository;
import com.v1no.LJL.learning_service.service.CardDeckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardDeckServiceImpl implements CardDeckService {

    private final CardDeckRepository cardDeckRepository;
    private final CardDeckMapper cardDeckMapper;

    @Override
    @Transactional
    public CardDeckResponse create(CardDeckRequest request) {
        log.info("Creating CardDeck with title: {}", request.title());
        CardDeck deck = cardDeckMapper.toEntity(request);
        CardDeck saved = cardDeckRepository.save(deck);
        log.info("CardDeck created with id: {}", saved.getId());
        return cardDeckMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CardDeckDetailResponse getById(Long id) {
        log.debug("Fetching CardDeck with id: {}", id);
        CardDeck deck = cardDeckRepository.findByIdWithCards(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find card with ID::%s".formatted(id)));
        return cardDeckMapper.toDetailResponse(deck);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardDeckResponse> getAll(LanguageCode language, Pageable pageable) {
        log.debug("Fetching all CardDecks, language filter: {}", language);
        if (language != null) {
            return cardDeckRepository.findAllByLanguage(language, pageable)
                    .map(cardDeckMapper::toResponse);
        }
        return cardDeckRepository.findAll(pageable)
                .map(cardDeckMapper::toResponse);
    }

    @Override
    @Transactional
    public CardDeckResponse update(Long id, CardDeckRequest request) {
        log.info("Updating CardDeck with id: {}", id);
        CardDeck deck = cardDeckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find card with ID::%s".formatted(id)));
        cardDeckMapper.updateEntity(deck, request);
        return cardDeckMapper.toResponse(deck); // @Transactional tự dirty-check và save
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting CardDeck with id: {}", id);
        if (!cardDeckRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot find card with ID::%s".formatted(id));
        }
        cardDeckRepository.deleteById(id);
    }
}