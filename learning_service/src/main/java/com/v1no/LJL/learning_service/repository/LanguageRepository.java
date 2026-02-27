package com.v1no.LJL.learning_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.v1no.LJL.learning_service.model.entity.Language;

public interface LanguageRepository extends JpaRepository<Language, String> {
    Optional<Language> findByCode(String code);
}
