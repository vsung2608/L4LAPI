package com.v1no.LJL.learning_service.repository;

import com.v1no.LJL.learning_service.model.entity.FlashCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashCardRepository extends JpaRepository<FlashCard, Long> {

    Page<FlashCard> findAllByDeckId(Long deckId, Pageable pageable);

    boolean existsByDeckId(Long deckId);
}