package com.v1no.LJL.learning_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v1no.LJL.common.dto.PageResponse;
import com.v1no.LJL.learning_service.model.dto.request.CreateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.response.LanguageCatalogResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonPreviewResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonSummaryResponse;
import com.v1no.LJL.learning_service.model.enums.JlptLevel;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;

public interface LessonService {

    LessonSummaryResponse create(CreateLessonRequest request);

    LessonSummaryResponse update(UUID id, UpdateLessonRequest request);

    void delete(UUID id);

    LessonSummaryResponse findById(UUID id);

    LessonDetailResponse findDetailById(UUID id);

    List<LessonPreviewResponse> findByCategoryId(UUID categoryId, UUID userId);

    List<LessonPreviewResponse> findByLevel(JlptLevel level);

    PageResponse<LessonPreviewResponse> findAll(Pageable pageable);

    LanguageCatalogResponse getCatalogByLanguage(LanguageCode languageCode, UUID userId);
}
