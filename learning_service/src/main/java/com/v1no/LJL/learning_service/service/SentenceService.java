package com.v1no.LJL.learning_service.service;

import java.util.List;
import java.util.UUID;

import com.v1no.LJL.learning_service.model.dto.request.CreateSentenceRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateSentenceRequest;
import com.v1no.LJL.learning_service.model.dto.response.SentenceResponse;

public interface SentenceService {

    List<SentenceResponse> create(List<CreateSentenceRequest> request, UUID lessonId);

    SentenceResponse update(UUID id, UpdateSentenceRequest request);

    void delete(UUID id);

    SentenceResponse findById(UUID id);

    List<SentenceResponse> findByLessonId(UUID lessonId);
}
