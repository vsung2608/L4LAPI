package com.v1no.LJL.learning_service.service;

import com.v1no.LJL.learning_service.model.dto.request.CardDeckRequest;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckResponse;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;

import java.util.List;
import java.util.UUID;

public interface CardDeckService {

    CardDeckResponse create(CardDeckRequest request);
    CardDeckDetailResponse getById(UUID id);
    List<CardDeckResponse> getAll(UUID userId,LanguageCode language);
    CardDeckResponse update(UUID id, CardDeckRequest request);
    void delete(UUID id);
}