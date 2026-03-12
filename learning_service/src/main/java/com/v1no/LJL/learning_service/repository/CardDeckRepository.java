package com.v1no.LJL.learning_service.repository;

import com.v1no.LJL.learning_service.model.entity.CardDeck;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardDeckRepository extends JpaRepository<CardDeck, Long> {

    Page<CardDeck> findAllByLanguage(LanguageCode language, Pageable pageable);

    // Fetch cards cùng lúc, tránh LazyInitializationException khi map detail
    @Query("SELECT d FROM CardDeck d LEFT JOIN FETCH d.cards WHERE d.id = :id")
    java.util.Optional<CardDeck> findByIdWithCards(@Param("id") Long id);
}