package com.v1no.LJL.learning_service.service;

import com.v1no.LJL.common.dto.PageResponse;
import com.v1no.LJL.learning_service.model.dto.request.CardDeckRequest;
import com.v1no.LJL.learning_service.model.dto.response.CardDeckResponse;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;

import java.util.List;
import java.util.UUID;

public interface CardDeckService {

    CardDeckResponse create(CardDeckRequest request);
    List<CardDeckResponse> getAll(UUID userId,LanguageCode language);
    PageResponse<CardDeckResponse> getAllForAdmin(int page, int size, String language);
    CardDeckResponse update(UUID id, CardDeckRequest request);
    void delete(UUID id);
}