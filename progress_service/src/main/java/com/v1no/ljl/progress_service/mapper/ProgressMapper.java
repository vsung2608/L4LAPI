package com.v1no.ljl.progress_service.mapper;

import org.springframework.stereotype.Component;

import com.v1no.ljl.progress_service.model.dto.request.StartLessonRequest;
import com.v1no.ljl.progress_service.model.dto.response.LessonProgressResponse;
import com.v1no.ljl.progress_service.model.entity.UserLessonProgress;

@Component
public class ProgressMapper {

    public UserLessonProgress toEntity(StartLessonRequest request) {
        return UserLessonProgress.builder()
            .userId(request.userId())
            .lessonId(request.lessonId())
            .build();
    }

    public LessonProgressResponse toResponse(UserLessonProgress progress) {
        return new LessonProgressResponse(
            progress.getId(),
            progress.getUserId(),
            progress.getLessonId(),
            progress.getStatus(),
            progress.getCurrentSentenceIndex(),
            progress.getStartedAt(),
            progress.getCompletedAt(),
            progress.getLastAccessedAt()
        );
    }
}