package com.v1no.LJL.learning_service.service;

import com.v1no.LJL.learning_service.model.dto.request.CardDeckRequest;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckResponse;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardDeckService {

    CardDeckResponse create(CardDeckRequest request);
    CardDeckDetailResponse getById(Long id);
    Page<CardDeckResponse> getAll(LanguageCode language, Pageable pageable);
    CardDeckResponse update(Long id, CardDeckRequest request);
    void delete(Long id);
}