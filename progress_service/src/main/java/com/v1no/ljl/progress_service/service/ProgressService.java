package com.v1no.ljl.progress_service.service;

import java.util.List;
import java.util.UUID;

import com.v1no.ljl.progress_service.model.dto.request.StartLessonRequest;
import com.v1no.ljl.progress_service.model.dto.request.UpdateProgressRequest;
import com.v1no.ljl.progress_service.model.dto.response.LessonProgressResponse;
import com.v1no.ljl.progress_service.model.enums.LessonStatus;

public interface ProgressService {

    LessonProgressResponse startLesson(StartLessonRequest request);

    LessonProgressResponse updateProgress(UUID userId, UUID lessonId, UpdateProgressRequest request);

    LessonProgressResponse getProgress(UUID userId, UUID lessonId);

    List<LessonProgressResponse> getUserProgress(UUID userId);

    List<LessonProgressResponse> getUserProgressByStatus(UUID userId, LessonStatus status);

    List<LessonProgressResponse> getProgressByLessonIds(UUID userId, List<UUID> lessonIds);
}
