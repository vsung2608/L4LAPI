package com.v1no.LJL.learning_service.service;

import com.v1no.LJL.learning_service.model.dto.response.PronunciationResultResponse;

public interface PronunciationService {
    PronunciationResultResponse assess(byte[] audioData, String referenceText, String language) throws Exception;
}
