package com.v1no.LJL.learning_service.mapper;


import java.util.List;

import org.springframework.stereotype.Component;

import com.v1no.LJL.common.dto.LessonProgressSummary;
import com.v1no.LJL.learning_service.model.dto.request.CreateCategoryRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateCategoryRequest;
import com.v1no.LJL.learning_service.model.dto.response.CategoryDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.CategorySummaryResponse;
import com.v1no.LJL.learning_service.model.dto.response.CategoryWithLessonsResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonPreviewResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonSummaryResponse;
import com.v1no.LJL.learning_service.model.entity.Category;
import com.v1no.LJL.learning_service.model.entity.Lesson;
import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;

import jakarta.persistence.EnumType;

@Component
public class CategoryMapper {

    private final LessonMapper lessonMapper;

    public CategoryMapper(LessonMapper lessonMapper) {
        this.lessonMapper = lessonMapper;
    }

    public Category toEntity(CreateCategoryRequest request) {
        return Category.builder()
            .name(request.name())
            .language(EnumType.valueOf(LanguageCode.class, request.languageCode()))
            .description(request.description())
            .thumbnailUrl(request.thumbnailUrl())
            .displayOrder(request.displayOrder())
            .status(ContentStatus.ACTIVE)
            .build();
    }

    public void updateEntity(Category category, UpdateCategoryRequest request) {
        category.setName(request.name());
        category.setDescription(request.description());
        category.setThumbnailUrl(request.thumbnailUrl());
        category.setDisplayOrder(request.displayOrder());
        category.setStatus(request.status());
    }

    public CategorySummaryResponse toSummary(Category category) {
        return new CategorySummaryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getLanguage().name(),
            category.getThumbnailUrl(),
            category.getDisplayOrder(),
            category.getStatus(),
            category.getCreatedAt()
        );
    }

    public CategoryDetailResponse toDetail(Category category) {
        List<LessonSummaryResponse> lessons = category.getLessons()
            .stream()
            .map(lesson -> lessonMapper.toSummary(lesson))
            .toList();

        return new CategoryDetailResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getThumbnailUrl(),
            category.getDisplayOrder(),
            category.getStatus(),
            lessons,
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }

    public CategoryWithLessonsResponse toCategoryWithLessons(
        Category category,
        List<LessonPreviewResponse> lessons
    ) {
        return new CategoryWithLessonsResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getThumbnailUrl(),
            category.getDisplayOrder(),
            lessons
        );
    }
}
