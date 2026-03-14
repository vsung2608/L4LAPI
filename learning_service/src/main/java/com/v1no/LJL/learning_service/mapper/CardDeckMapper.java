package com.v1no.LJL.learning_service.mapper;

import com.v1no.LJL.learning_service.model.dto.request.CardDeckRequest;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckResponse;
import com.v1no.LJL.learning_service.model.entity.CardDeck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CardDeckMapper {

    private final FlashCardMapper flashCardMapper;

    public CardDeck toEntity(CardDeckRequest request) {
        return CardDeck.builder()
                .title(request.title())
                .description(request.description())
                .language(request.language())
                .build();
    }

    public void updateEntity(CardDeck deck, CardDeckRequest request) {
        deck.setTitle(request.title());
        deck.setDescription(request.description());
        deck.setLanguage(request.language());
    }

    public CardDeckResponse toResponse(CardDeck deck, Boolean isStarted) {
        return CardDeckResponse.builder()
                .id(deck.getId())
                .title(deck.getTitle())
                .thumbnailUrl(deck.getThumbnailUrl())
                .description(deck.getDescription())
                .language(deck.getLanguage())
                .started(isStarted)
                .totalCards(deck.getCards().size())
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .build();
    }

    public CardDeckDetailResponse toDetailResponse(CardDeck deck) {
        return CardDeckDetailResponse.builder()
                .id(deck.getId())
                .title(deck.getTitle())
                .thumbnailUrl(deck.getThumbnailUrl())
                .description(deck.getDescription())
                .language(deck.getLanguage())
                .cards(flashCardMapper.toResponseList(deck.getCards()))
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .build();
    }
}