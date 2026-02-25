package com.v1no.LJL.learning_service.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.common.dto.PageResponse;
import com.v1no.LJL.learning_service.exception.BusinessException;
import com.v1no.LJL.learning_service.exception.ResourceNotFoundException;
import com.v1no.LJL.learning_service.mapper.LessonMapper;
import com.v1no.LJL.learning_service.model.dto.request.CreateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.response.LessonDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonSummaryResponse;
import com.v1no.LJL.learning_service.model.entity.Category;
import com.v1no.LJL.learning_service.model.entity.Lesson;
import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.model.enums.JlptLevel;
import com.v1no.LJL.learning_service.repository.CategoryRepository;
import com.v1no.LJL.learning_service.repository.LessonRepository;
import com.v1no.LJL.learning_service.service.LessonService;
import com.v1no.LJL.learning_service.util.YoutubeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CategoryRepository categoryRepository;
    private final LessonMapper lessonMapper;

    @Override
    public LessonSummaryResponse create(CreateLessonRequest request) {
        log.info("Creating lesson: title={}, categoryId={}", request.title(), request.categoryId());

        Category category = findCategoryById(request.categoryId());

        String videoId = YoutubeUtil.extractVideoId(request.youtubeVideoUrl());
        Integer videoDuration = YoutubeUtil.getVideoDurationInSeconds(videoId);
        String thumbnailUrl = YoutubeUtil.getThumbnailUrl(videoId);

        Lesson saved = lessonRepository.save(lessonMapper.toEntity(request, category, videoId, thumbnailUrl, videoDuration));

        log.info("Lesson created: id={}", saved.getId());
        return lessonMapper.toSummary(saved);
    }

    @Override
    public LessonSummaryResponse update(UUID id, UpdateLessonRequest request) {
        log.info("Updating lesson: id={}", id);

        Lesson lesson = findLessonById(id);
        Category category = findCategoryById(request.categoryId());

        lessonMapper.updateEntity(lesson, request, category);
        Lesson saved = lessonRepository.save(lesson);

        log.info("Lesson updated: id={}", saved.getId());
        return lessonMapper.toSummary(saved);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting lesson: id={}", id);

        Lesson lesson = findLessonById(id);

        if (!lesson.getSentences().isEmpty()) {
            throw new BusinessException("Cannot delete lesson that still has sentences");
        }

        lessonRepository.delete(lesson);
        log.info("Lesson deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonSummaryResponse findById(UUID id) {
        Lesson lesson = lessonRepository.findByIdWithCategory(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + id));
        return lessonMapper.toSummary(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonDetailResponse findDetailById(UUID id) {
        Lesson lesson = lessonRepository.findActiveByIdWithSentences(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lesson not found or not active: " + id));
        return lessonMapper.toDetail(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonSummaryResponse> findByCategoryId(UUID categoryId) {
        // Validate category tồn tại trước
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }
        return lessonRepository.findAllByCategoryIdAndStatus(categoryId, ContentStatus.ACTIVE)
            .stream()
            .map(lessonMapper::toSummary)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonSummaryResponse> findByLevel(JlptLevel level) {
        return lessonRepository.findActiveByLevelWithCategory(level)
            .stream()
            .map(lessonMapper::toSummary)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LessonSummaryResponse> findAll(Pageable pageable) {
        Page<Lesson> page = lessonRepository.findAllActive(pageable);
        return PageResponse.<LessonSummaryResponse>builder()
            .data(page.getContent().stream().map(lessonMapper::toSummary).toList())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .page(page.getNumber())
            .size(page.getSize())
            .build();
    }

    private Lesson findLessonById(UUID id) {
        return lessonRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + id));
    }

    private Category findCategoryById(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }
}