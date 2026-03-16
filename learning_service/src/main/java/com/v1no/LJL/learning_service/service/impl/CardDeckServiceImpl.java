package com.v1no.LJL.learning_service.service.impl;

import com.v1no.LJL.common.dto.DeckStudiedResponse;
import com.v1no.LJL.common.dto.PageResponse;
import com.v1no.LJL.common.exception.ResourceNotFoundException;
import com.v1no.LJL.learning_service.client.ProgressServiceClient;
import com.v1no.LJL.learning_service.mapper.CardDeckMapper;
import com.v1no.LJL.learning_service.model.dto.request.CardDeckRequest;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckResponse;
import com.v1no.LJL.learning_service.model.dto.response.FileUploadResponse;
import com.v1no.LJL.learning_service.model.entity.CardDeck;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import com.v1no.LJL.learning_service.repository.CardDeckRepository;
import com.v1no.LJL.learning_service.service.CardDeckService;
import com.v1no.LJL.learning_service.util.CloudinaryUtil;

import jakarta.persistence.EnumType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardDeckServiceImpl implements CardDeckService {

    private final CloudinaryUtil cloudinaryUtil;
    private final CardDeckRepository cardDeckRepository;
    private final CardDeckMapper cardDeckMapper;
    private final ProgressServiceClient progressServiceClient;

    @Override
    @Transactional
    public CardDeckResponse create(CardDeckRequest request) {
        log.info("Creating CardDeck with title: {}", request.title());
        CardDeck deck = cardDeckMapper.toEntity(request);
        try {
            FileUploadResponse file = cloudinaryUtil.uploadImage(request.thumbnail().getBytes());
            deck.setThumbnailUrl(file.getSecureUrl());
            CardDeck saved = cardDeckRepository.save(deck);
            log.info("CardDeck created with id: {}", saved.getId());
            return cardDeckMapper.toResponse(saved, false);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<CardDeckResponse> getAll(UUID userId, LanguageCode language) {
         log.debug("Fetching all CardDecks, userId: {}, language filter: {}", userId, language);

        List<CardDeck> decks = language != null
                ? cardDeckRepository.findAllByLanguage(language)
                : cardDeckRepository.findAll();

        if (decks.isEmpty()) {
            return decks.stream().map(deck -> cardDeckMapper.toResponse(null, false)).toList();
        }

        List<UUID> deckIds = decks.stream()
                .map(CardDeck::getId)
                .toList();

        Map<UUID, Boolean> progressMap = progressServiceClient
                .getStudiedDecks(userId, deckIds)
                .getData()
                .stream()
                .collect(Collectors.toMap(DeckStudiedResponse::deckId, DeckStudiedResponse::started));

        return decks.stream()
                .map(deck -> cardDeckMapper.toResponse(deck, progressMap.getOrDefault(deck.getId(), false)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CardDeckResponse> getAllForAdmin(int page, int size, String language){
        LanguageCode languageCode = EnumType.valueOf(LanguageCode.class, language);
        Pageable pageable = PageRequest.of(size, page - 1);
        Page<CardDeck> pageResponse = cardDeckRepository.findAllByLanguage(languageCode, pageable);
        return PageResponse.<CardDeckResponse>builder()
                .data(pageResponse.getContent().stream()
                        .map(c -> cardDeckMapper.toResponse(c, false))
                        .toList())
                .page(page)
                .size(size)
                .totalElements(pageResponse.getTotalElements())
                .totalPages(pageResponse.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public CardDeckResponse update(UUID id, CardDeckRequest request) {
        log.info("Updating CardDeck with id: {}", id);
        CardDeck deck = cardDeckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find card with ID::%s".formatted(id)));
        cardDeckMapper.updateEntity(deck, request);
        return cardDeckMapper.toResponse(deck, false);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting CardDeck with id: {}", id);
        if (!cardDeckRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot find card with ID::%s".formatted(id));
        }
        cardDeckRepository.deleteById(id);
    }
}