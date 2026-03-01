package com.v1no.LJL.learning_service.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.common.dto.LessonProgressSummary;
import com.v1no.LJL.common.dto.PageResponse;
import com.v1no.LJL.learning_service.client.ProgressServiceClient;
import com.v1no.LJL.learning_service.custom.validate.LevelValidator;
import com.v1no.LJL.learning_service.exception.BusinessException;
import com.v1no.LJL.learning_service.exception.ResourceNotFoundException;
import com.v1no.LJL.learning_service.mapper.CategoryMapper;
import com.v1no.LJL.learning_service.mapper.LessonMapper;
import com.v1no.LJL.learning_service.model.dto.request.CreateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateLessonRequest;
import com.v1no.LJL.learning_service.model.dto.response.CategoryWithLessonsResponse;
import com.v1no.LJL.learning_service.model.dto.response.LanguageCatalogResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonDetailResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonPreviewResponse;
import com.v1no.LJL.learning_service.model.dto.response.LessonSummaryResponse;
import com.v1no.LJL.learning_service.model.dto.response.YoutubeVideoInfo;
import com.v1no.LJL.learning_service.model.entity.Category;
import com.v1no.LJL.learning_service.model.entity.Language;
import com.v1no.LJL.learning_service.model.entity.Lesson;
import com.v1no.LJL.learning_service.model.enums.ContentStatus;
import com.v1no.LJL.learning_service.model.enums.JlptLevel;
import com.v1no.LJL.learning_service.repository.CategoryRepository;
import com.v1no.LJL.learning_service.repository.LanguageRepository;
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
    private static final int PREVIEW_LESSON_LIMIT = 5;

    private final LessonRepository lessonRepository;
    private final CategoryRepository categoryRepository;
    private final LanguageRepository languageRepository;
    private final CategoryMapper categoryMapper;
    private final LessonMapper lessonMapper;
    private final ProgressServiceClient progressServiceClient;
    private final LevelValidator levelValidator;

    @Override
    public LessonSummaryResponse create(CreateLessonRequest request) {
        log.info("Creating lesson: categoryId={}", request.categoryId());

        Category category = findCategoryById(request.categoryId());

        String videoId = YoutubeUtil.extractVideoId(request.youtubeVideoUrl());
        YoutubeVideoInfo info = YoutubeUtil.getVideoInfo(videoId);

        levelValidator.validate(category.getLanguage().getCode(), request.level());

        Lesson saved = lessonRepository.save(lessonMapper.toEntity(request, category, videoId, info));

        log.info("Lesson created: id={}", saved.getId());
        return lessonMapper.toSummary(saved, null);
    }

    @Override
    public LessonSummaryResponse update(UUID id, UpdateLessonRequest request) {
        log.info("Updating lesson: id={}", id);

        Lesson lesson = findLessonById(id);
        Category category = findCategoryById(request.categoryId());

        lessonMapper.updateEntity(lesson, request, category);
        Lesson saved = lessonRepository.save(lesson);

        log.info("Lesson updated: id={}", saved.getId());
        return lessonMapper.toSummary(saved, null);
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
        return lessonMapper.toSummary(lesson, null);
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
    public List<LessonSummaryResponse> findByCategoryId(UUID categoryId, UUID userId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        }

        List<Lesson> lessons = lessonRepository
            .findAllByCategoryIdAndStatus(categoryId, ContentStatus.ACTIVE);

        if (lessons.isEmpty()) return List.of();

        // Bulk fetch progress — 1 call duy nhất, không N+1
        List<UUID> lessonIds = lessons.stream()
            .map(Lesson::getId)
            .toList();

        Map<UUID, LessonProgressSummary> progressMap = progressServiceClient
            .getProgressByLessonIds(userId, lessonIds)
            .getData()
            .stream()
            .collect(Collectors.toMap(LessonProgressSummary::lessonId, Function.identity()));

        return lessons.stream()
            .map(lesson -> lessonMapper.toSummary(
                lesson,
                progressMap.get(lesson.getId())
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonSummaryResponse> findByLevel(JlptLevel level) {
        return lessonRepository.findActiveByLevelWithCategory(level)
            .stream()
            .map(lesson -> lessonMapper.toSummary(lesson, null))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LessonSummaryResponse> findAll(Pageable pageable) {
        Page<Lesson> page = lessonRepository.findAllActive(pageable);
        return PageResponse.<LessonSummaryResponse>builder()
            .data(page.getContent().stream().map(lesson -> lessonMapper.toSummary(lesson, null)).toList())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .page(page.getNumber())
            .size(page.getSize())
            .build();
    }

    @Override
    public LanguageCatalogResponse getCatalogByLanguage(String languageCode, UUID userId) {
        log.info("Getting catalog: languageCode={}, userId={}", languageCode, userId);

        Language language = languageRepository.findByCode(languageCode)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Language not found: " + languageCode
            ));

        List<Category> categories = categoryRepository
            .findActiveByLanguageCode(languageCode);

        if (categories.isEmpty()) {
            return new LanguageCatalogResponse(
                language.getCode(),
                language.getName(),
                language.getNativeName(),
                List.of()
            );
        }

        List<UUID> categoryIds = categories.stream()
            .map(Category::getId)
            .toList();

        List<Lesson> allLessons = lessonRepository.findActiveByCategoryIdIn(categoryIds);

        Map<UUID, List<Lesson>> lessonsByCategoryId = allLessons.stream()
            .collect(Collectors.groupingBy(
                lesson -> lesson.getCategory().getId(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> list.stream()
                        .limit(PREVIEW_LESSON_LIMIT)
                        .toList()
                )
            ));

        List<UUID> lessonIds = allLessons.stream()
            .map(Lesson::getId)
            .toList();

        Map<UUID, LessonProgressSummary> progressMap = fetchProgressMap(userId, lessonIds);

        List<CategoryWithLessonsResponse> categoryResponses = categories.stream()
            .map(category -> {
                List<LessonPreviewResponse> lessonPreviews = lessonsByCategoryId
                    .getOrDefault(category.getId(), List.of())
                    .stream()
                    .map(lesson -> lessonMapper.toPreview(
                        lesson,
                        progressMap.get(lesson.getId())
                    ))
                    .toList();

                return categoryMapper.toCategoryWithLessons(category, lessonPreviews);
            })
            .toList();

        return new LanguageCatalogResponse(
            language.getCode(),
            language.getName(),
            language.getNativeName(),
            categoryResponses
        );
    }

    private Map<UUID, LessonProgressSummary> fetchProgressMap(
        UUID userId, List<UUID> lessonIds
    ) {
        if (lessonIds.isEmpty()) return Map.of();
        try {
            return progressServiceClient
                .getProgressByLessonIds(userId, lessonIds)
                .getData()
                .stream()
                .collect(Collectors.toMap(
                    LessonProgressSummary::lessonId,
                    Function.identity()
                ));
        } catch (Exception e) {
            log.warn("Failed to fetch progress, continuing without it: {}", e.getMessage());
            return Map.of();
        }
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