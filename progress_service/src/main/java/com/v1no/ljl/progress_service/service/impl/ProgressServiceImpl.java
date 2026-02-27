package com.v1no.ljl.progress_service.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.common.dto.LessonProgressSummary;
import com.v1no.LJL.common.exception.BusinessException;
import com.v1no.LJL.common.exception.ResourceNotFoundException;
import com.v1no.ljl.progress_service.mapper.ProgressMapper;
import com.v1no.ljl.progress_service.model.dto.request.StartLessonRequest;
import com.v1no.ljl.progress_service.model.dto.request.UpdateProgressRequest;
import com.v1no.ljl.progress_service.model.dto.response.LessonProgressResponse;
import com.v1no.ljl.progress_service.model.entity.UserLessonProgress;
import com.v1no.ljl.progress_service.model.enums.LearningMode;
import com.v1no.ljl.progress_service.model.enums.LessonStatus;
import com.v1no.ljl.progress_service.repository.UserLessonProgressRepository;
import com.v1no.ljl.progress_service.service.ProgressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgressServiceImpl implements ProgressService {

    private final UserLessonProgressRepository progressRepository;
    private final ProgressMapper progressMapper;

    @Override
    public LessonProgressResponse startLesson(StartLessonRequest request) {
        log.info("Starting lesson: userId={}, lessonId={}, mode={}",
            request.userId(), request.lessonId(), request.mode());

        return progressRepository
            .findByUserIdAndLessonIdAndMode(request.userId(), request.lessonId(), request.mode())
            .map(existing -> {
                log.info("Progress already exists: id={}", existing.getId());
                return progressMapper.toResponse(existing);
            })
            .orElseGet(() -> {
                UserLessonProgress saved = progressRepository.save(
                    progressMapper.toEntity(request)
                );
                log.info("Progress created: id={}", saved.getId());
                return progressMapper.toResponse(saved);
            });
    }

    @Override
    public LessonProgressResponse updateProgress(
        UUID userId, UUID lessonId, LearningMode mode,
        UpdateProgressRequest request
    ) {
        UserLessonProgress progress = progressRepository
            .findByUserIdAndLessonIdAndMode(userId, lessonId, mode)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Progress not found: userId=%s, lessonId=%s, mode=%s"
                    .formatted(userId, lessonId, mode)
            ));

        if (progress.isCompleted()) {
            throw new BusinessException("Cannot update a completed lesson progress");
        }

        progress.advanceTo(request.currentSentenceIndex());

        if (LessonStatus.COMPLETED.equals(request.status())) {
            progress.complete();
        }

        return progressMapper.toResponse(progressRepository.save(progress));
    }

    @Override
    @Transactional(readOnly = true)
    public LessonProgressResponse getProgress(UUID userId, UUID lessonId) {
        return progressMapper.toResponse(findByUserAndLesson(userId, lessonId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonProgressResponse> getUserProgress(UUID userId) {
        return progressRepository
            .findAllByUserIdOrderByLastAccessedAtDesc(userId)
            .stream()
            .map(progressMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonProgressResponse> getUserProgressByStatus(
        UUID userId,
        LessonStatus status
    ) {
        return progressRepository
            .findAllByUserIdAndStatus(userId, status)
            .stream()
            .map(progressMapper::toResponse)
            .toList();
    }

     private UserLessonProgress findByUserAndLesson(UUID userId, UUID lessonId) {
        return progressRepository
            .findAllByUserIdAndLessonId(userId, lessonId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(
                "Progress not found for userId=%s and lessonId=%s"
                    .formatted(userId, lessonId)
            ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonProgressSummary> getProgressByLessonIds(
        UUID userId, List<UUID> lessonIds
    ) {
        List<UserLessonProgress> all = progressRepository
            .findAllByUserIdAndLessonIdIn(userId, lessonIds);

        return all.stream()
            .collect(Collectors.groupingBy(UserLessonProgress::getLessonId))
            .entrySet()
            .stream()
            .map(entry -> progressMapper.toSummary(entry.getKey(), entry.getValue()))
            .toList();
    }
}