package com.v1no.LJL.learning_service.repository;

import com.v1no.LJL.learning_service.model.entity.CardDeck;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardDeckRepository extends JpaRepository<CardDeck, UUID> {

    List<CardDeck> findAllByLanguage(LanguageCode language);

    // Fetch cards cùng lúc, tránh LazyInitializationException khi map detail
    @Query("SELECT d FROM CardDeck d LEFT JOIN FETCH d.cards WHERE d.id = :id")
    java.util.Optional<CardDeck> findByIdWithCards(@Param("id") UUID id);
}