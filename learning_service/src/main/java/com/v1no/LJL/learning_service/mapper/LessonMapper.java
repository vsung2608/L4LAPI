package com.v1no.LJL.learning_service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.v1no.LJL.common.dto.LessonProgressSummary;
import com.v1no.LJL.learning_service.model.dto.request.CreateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.response.LessonDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonPreviewResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonSummaryResponse;
import com.v1no.LJL.learning_service.model.dto.response.SentenceResponse;
import com.v1no.LJL.learning_service.model.entity.Category;
import com.v1no.LJL.learning_service.model.entity.Lesson;
import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.model.dto.response.YoutubeVideoInfo;

@Component
public class LessonMapper {

    private final SentenceMapper sentenceMapper;

    public LessonMapper(SentenceMapper sentenceMapper) {
        this.sentenceMapper = sentenceMapper;
    }

    public Lesson toEntity(CreateLessonRequest request, Category category, String youtubeVideoId, YoutubeVideoInfo info) {
        return Lesson.builder()
            .category(category)
            .title(info.title())
            .description(request.description())
            .thumbnailUrl(info.thumbnailUrl())
            .durationSeconds(info.durationSeconds())
            .youtubeVideoId(youtubeVideoId)
            .level(request.level())
            .displayOrder(request.displayOrder())
            .status(ContentStatus.DRAFT)
            .build();
    }

    public void updateEntity(Lesson lesson, UpdateLessonRequest request, Category category) {
        lesson.setCategory(category);
        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setThumbnailUrl(request.thumbnailUrl());
        lesson.setYoutubeVideoId(request.youtubeVideoId());
        lesson.setDurationSeconds(request.durationSeconds());
        lesson.setLevel(request.level());
        lesson.setDisplayOrder(request.displayOrder());
        lesson.setStatus(request.status());
    }

    public LessonSummaryResponse toSummary(Lesson lesson, LessonProgressSummary progress) {
        return new LessonSummaryResponse(
            lesson.getId(),
            lesson.getCategory().getId(),
            lesson.getCategory().getName(),
            lesson.getTitle(),
            lesson.getDescription(),
            lesson.getThumbnailUrl(),
            lesson.getYoutubeVideoId(),
            lesson.getDurationSeconds(),
            lesson.getLevel(),
            lesson.getDisplayOrder(),
            lesson.getStatus(),
            lesson.getTotalSentences(),
            lesson.getCreatedAt(),
            progress
        );
    }

    public LessonDetailResponse toDetail(Lesson lesson) {
        List<SentenceResponse> sentences = lesson.getSentences()
            .stream()
            .map(sentenceMapper::toResponse)
            .toList();

        return new LessonDetailResponse(
            lesson.getId(),
            lesson.getCategory().getId(),
            lesson.getCategory().getName(),
            lesson.getTitle(),
            lesson.getDescription(),
            lesson.getThumbnailUrl(),
            lesson.getYoutubeVideoId(),
            lesson.getDurationSeconds(),
            lesson.getLevel(),
            lesson.getDisplayOrder(),
            lesson.getStatus(),
            sentences,
            lesson.getCreatedAt(),
            lesson.getUpdatedAt()
        );
    }

    public LessonPreviewResponse toPreview(Lesson lesson, LessonProgressSummary progress) {
        return new LessonPreviewResponse(
            lesson.getId(),
            lesson.getTitle(),
            lesson.getThumbnailUrl(),
            lesson.getYoutubeVideoId(),
            lesson.getDurationSeconds(),
            lesson.getLevel(),
            lesson.getDisplayOrder(),
            lesson.getTotalSentences(),
            progress
        );
    }
}
