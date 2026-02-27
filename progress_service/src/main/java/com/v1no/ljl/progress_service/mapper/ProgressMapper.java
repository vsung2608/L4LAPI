package com.v1no.ljl.progress_service.mapper;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.v1no.LJL.common.dto.LessonProgressSummary;
import com.v1no.ljl.progress_service.model.dto.request.StartLessonRequest;
import com.v1no.ljl.progress_service.model.dto.response.LessonProgressResponse;
import com.v1no.ljl.progress_service.model.entity.UserLessonProgress;
import com.v1no.ljl.progress_service.model.enums.LearningMode;

@Component
public class ProgressMapper {

    public UserLessonProgress toEntity(StartLessonRequest request) {
        return UserLessonProgress.builder()
            .userId(request.userId())
            .lessonId(request.lessonId())
            .mode(request.mode())
            .build();
    }

    public LessonProgressResponse toResponse(UserLessonProgress progress) {
        return new LessonProgressResponse(
            progress.getId(),
            progress.getUserId(),
            progress.getLessonId(),
            progress.getMode(),
            progress.getStatus(),
            progress.getCurrentSentenceIndex(),
            progress.getStartedAt(),
            progress.getCompletedAt(),
            progress.getLastAccessedAt()
        );
    }

    public LessonProgressSummary toSummary(UUID lessonId, List<UserLessonProgress> progresses) {
        LessonProgressSummary.LessonProgressDetail dictation = progresses.stream()
            .filter(p -> LearningMode.DICTATION.equals(p.getMode()))
            .findFirst()
            .map(this::toDetail)
            .orElse(null);

        LessonProgressSummary.LessonProgressDetail speaking = progresses.stream()
            .filter(p -> LearningMode.SPEAKING.equals(p.getMode()))
            .findFirst()
            .map(this::toDetail)
            .orElse(null);

        return new LessonProgressSummary(lessonId, dictation, speaking);
    }

    private LessonProgressSummary.LessonProgressDetail toDetail(UserLessonProgress p) {
        return new LessonProgressSummary.LessonProgressDetail(
            p.getStatus().name(),
            p.getCurrentSentenceIndex(),
            p.getLastAccessedAt()
        );
    }
}