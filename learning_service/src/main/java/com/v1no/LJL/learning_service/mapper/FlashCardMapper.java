package com.v1no.LJL.learning_service.mapper;

import com.v1no.LJL.common.dto.CardProgressResponse;
import com.v1no.LJL.learning_service.model.dto.request.FlashCardRequest;
import com.v1no.LJL.learning_service.model.dto.response.FlashCardResponse;
import com.v1no.LJL.learning_service.model.entity.CardDeck;
import com.v1no.LJL.learning_service.model.entity.FlashCard;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlashCardMapper {

    public FlashCard toEntity(FlashCardRequest request, CardDeck deck) {
        return FlashCard.builder()
                .content(request.content())
                .transcription(request.transcription())
                .translation(request.translation())
                .exampleLang(request.exampleLang())
                .exampleViet(request.exampleViet())
                .explaintLang(request.explaintLang())
                .explaintViet(request.explaintViet())
                .hint(request.hint())
                .difficulty(request.difficulty())
                .displayOrder(request.displayOrder())
                .deck(deck)
                .build();
    }

    public void updateEntity(FlashCard card, FlashCardRequest request) {
        card.setContent(request.content());
        card.setTranscription(request.transcription());
        card.setTranslation(request.translation());
        card.setExampleLang(request.exampleLang());
        card.setExampleViet(request.exampleViet());
        card.setExplaintLang(request.explaintLang());
        card.setExplaintViet(request.explaintViet());
        card.setHint(request.hint());
        card.setDifficulty(request.difficulty());
        card.setDisplayOrder(request.displayOrder());
    }

    public FlashCardResponse toResponse(FlashCard card, CardProgressResponse progress) {
        return FlashCardResponse.builder()
                .id(card.getId())
                .content(card.getContent())
                .thumbnail(card.getThumbnailUrl())
                .transcription(card.getTranscription())
                .translation(card.getTranslation())
                .exampleLang(card.getExampleLang())
                .exampleViet(card.getExampleViet())
                .explaintLang(card.getExplaintLang())
                .explaintViet(card.getExplaintViet())
                .hint(card.getHint())
                .difficulty(card.getDifficulty())
                .displayOrder(card.getDisplayOrder())
                .mark(progress != null ? progress.mark() : null)
                .studyCount(progress != null ? progress.studyCount() : null)
                .deckId(card.getDeck().getId())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

}