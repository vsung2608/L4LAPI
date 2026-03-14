package com.v1no.LJL.learning_service.repository;

import com.v1no.LJL.learning_service.model.entity.FlashCard;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashCardRepository extends JpaRepository<FlashCard, UUID> {

    List<FlashCard> findAllByDeckId(UUID deckId);

    boolean existsByDeckId(UUID deckId);
}