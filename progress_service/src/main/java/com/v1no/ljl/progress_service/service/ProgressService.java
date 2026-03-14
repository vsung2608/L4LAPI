package com.v1no.ljl.progress_service.service;

import java.util.List;
import java.util.UUID;

import com.v1no.LJL.common.dto.LessonProgressSummary;
import com.v1no.ljl.progress_service.model.dto.request.StartLessonRequest;
import com.v1no.ljl.progress_service.model.dto.request.StudyRecordRequest;
import com.v1no.ljl.progress_service.model.dto.request.UpdateProgressRequest;
import com.v1no.ljl.progress_service.model.dto.response.DeckProgressResponse;
import com.v1no.ljl.progress_service.model.dto.response.DeckStudiedResponse;
import com.v1no.ljl.progress_service.model.dto.response.LessonProgressResponse;
import com.v1no.ljl.progress_service.model.dto.response.StudyRecordResponse;
import com.v1no.ljl.progress_service.model.enums.LearningMode;
import com.v1no.ljl.progress_service.model.enums.LessonStatus;

public interface ProgressService {

    LessonProgressResponse startLesson(StartLessonRequest request);

    LessonProgressResponse updateProgress(UUID userId, UUID lessonId, LearningMode mode, UpdateProgressRequest request);

    LessonProgressResponse getProgress(UUID userId, UUID lessonId);

    List<LessonProgressResponse> getUserProgress(UUID userId);

    List<LessonProgressResponse> getUserProgressByStatus(UUID userId, LessonStatus status);

    List<LessonProgressSummary> getProgressByLessonIds(UUID userId, List<UUID> lessonIds);

    StudyRecordResponse recordCard(UUID userId, UUID deckId,
                                   UUID cardId, StudyRecordRequest request);

    DeckProgressResponse getDeckProgress(UUID userId, UUID deckId);

    List<DeckStudiedResponse> getStudiedDecks(UUID userId, List<UUID> deckIds);
}
