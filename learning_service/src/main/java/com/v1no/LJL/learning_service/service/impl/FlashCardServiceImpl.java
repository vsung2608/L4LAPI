package com.v1no.LJL.learning_service.service.impl;

import com.v1no.LJL.common.dto.CardProgressResponse;
import com.v1no.LJL.common.dto.DeckProgressResponse;
import com.v1no.LJL.common.dto.PageResponse;
import com.v1no.LJL.common.exception.ResourceNotFoundException;
import com.v1no.LJL.learning_service.client.ProgressServiceClient;
import com.v1no.LJL.learning_service.mapper.FlashCardMapper;
import com.v1no.LJL.learning_service.model.dto.request.FlashCardRequest;
import com.v1no.LJL.learning_service.model.dto.response.FileUploadResponse;
import com.v1no.LJL.learning_service.model.dto.response.FlashCardResponse;
import com.v1no.LJL.learning_service.model.entity.CardDeck;
import com.v1no.LJL.learning_service.model.entity.FlashCard;
import com.v1no.LJL.learning_service.repository.CardDeckRepository;
import com.v1no.LJL.learning_service.repository.FlashCardRepository;
import com.v1no.LJL.learning_service.service.FlashCardService;
import com.v1no.LJL.learning_service.util.CloudinaryUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlashCardServiceImpl implements FlashCardService {

    private final CloudinaryUtil cloudinaryUtil;
    private final FlashCardRepository flashCardRepository;
    private final CardDeckRepository cardDeckRepository;
    private final FlashCardMapper flashCardMapper;
    private final ProgressServiceClient progressServiceClient;

    @Override
    @Transactional
    public FlashCardResponse create(UUID deckId, FlashCardRequest request) {
        log.info("Creating FlashCard in deck: {}", deckId);
        CardDeck deck = cardDeckRepository.findById(deckId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find flashcard with ID::%s".formatted(deckId)));
        FlashCard card = flashCardMapper.toEntity(request, deck);
        try {
            FileUploadResponse file = cloudinaryUtil.uploadImage(request.thumbnail().getBytes());
            card.setThumbnailUrl(file.getSecureUrl());
            FlashCard saved = flashCardRepository.save(card);
            log.info("FlashCard created with id: {}", saved.getId());
            return flashCardMapper.toResponse(saved, null);
        } catch (IOException e) {
            throw new RuntimeException("Cannot upload file image");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashCardResponse> getAllByDeck(UUID userId, UUID deckId) {
        log.debug("Fetching FlashCards for deck: {}, userId: {}", deckId, userId);

        if (!cardDeckRepository.existsById(deckId)) {
            throw new ResourceNotFoundException(
                    "Cannot find card deck with ID::%s".formatted(deckId));
        }

        List<FlashCard> cards = flashCardRepository.findAllByDeckId(deckId);

        if (cards.isEmpty()) {
            return List.of();
        }

        DeckProgressResponse deckProgress = progressServiceClient
                .getDeckProgress(userId, deckId)
                .getData();

        Map<UUID, CardProgressResponse> progressMap = deckProgress.records()
                .stream()
                .collect(Collectors.toMap(CardProgressResponse::flashCardId, Function.identity()));

        return cards.stream()
                .map(card -> flashCardMapper.toResponse(card, progressMap.get(card.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FlashCardResponse> getAllByDeckForAdmin(int page, int size, UUID deckId){
        if (!cardDeckRepository.existsById(deckId)) {
            throw new ResourceNotFoundException(
                    "Cannot find card deck with ID::%s".formatted(deckId));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<FlashCard> pageResponse = flashCardRepository.findAllByDeckId(deckId, pageable);

        return PageResponse.<FlashCardResponse>builder()
            .data(pageResponse.getContent().stream()
                    .map(fl -> flashCardMapper.toResponse(fl, null))
                    .toList())
            .page(page)
            .totalElements(pageResponse.getTotalElements())
            .totalPages(pageResponse.getTotalPages())
            .size(size)
            .build();
    }

    @Override
    @Transactional
    public FlashCardResponse update(UUID id, FlashCardRequest request) {
        log.info("Updating FlashCard with id: {}", id);
        FlashCard card = flashCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find flashcard with ID::%s".formatted(id)));
        flashCardMapper.updateEntity(card, request);
        return flashCardMapper.toResponse(card, null);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting FlashCard with id: {}", id);
        if (!flashCardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot find flashcard with ID::%s".formatted(id));
        }
        flashCardRepository.deleteById(id);
    }
}