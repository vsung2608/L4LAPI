package com.v1no.ljl.progress_service.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.common.dto.LessonProgressSummary;
import com.v1no.LJL.common.exception.BusinessException;
import com.v1no.LJL.common.exception.ResourceNotFoundException;
import com.v1no.ljl.progress_service.mapper.ProgressMapper;
import com.v1no.ljl.progress_service.mapper.StudyProgressMapper;
import com.v1no.ljl.progress_service.model.dto.request.StartLessonRequest;
import com.v1no.ljl.progress_service.model.dto.request.StudyRecordRequest;
import com.v1no.ljl.progress_service.model.dto.request.UpdateProgressRequest;
import com.v1no.ljl.progress_service.model.dto.response.DeckProgressResponse;
import com.v1no.ljl.progress_service.model.dto.response.DeckStudiedResponse;
import com.v1no.ljl.progress_service.model.dto.response.LessonProgressResponse;
import com.v1no.ljl.progress_service.model.dto.response.StudyRecordResponse;
import com.v1no.ljl.progress_service.model.entity.UserCardRecord;
import com.v1no.ljl.progress_service.model.entity.UserDeckProgress;
import com.v1no.ljl.progress_service.model.entity.UserLessonProgress;
import com.v1no.ljl.progress_service.model.enums.LearningMode;
import com.v1no.ljl.progress_service.model.enums.LessonStatus;
import com.v1no.ljl.progress_service.repository.UserCardRecordRepository;
import com.v1no.ljl.progress_service.repository.UserDeckProgressRepository;
import com.v1no.ljl.progress_service.repository.UserLessonProgressRepository;
import com.v1no.ljl.progress_service.service.ProgressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgressServiceImpl implements ProgressService {

    private final UserDeckProgressRepository userDeckProgressRepository;
    private final UserCardRecordRepository cardRecordRepository;
    private final StudyProgressMapper mapper;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final ProgressMapper progressMapper;

    @Override
    public LessonProgressResponse startLesson(StartLessonRequest request) {
        log.info("Starting lesson: userId={}, lessonId={}, mode={}",
            request.userId(), request.lessonId(), request.mode());

        return userLessonProgressRepository
            .findByUserIdAndLessonIdAndMode(request.userId(), request.lessonId(), request.mode())
            .map(existing -> {
                log.info("Progress already exists: id={}", existing.getId());
                return progressMapper.toResponse(existing);
            })
            .orElseGet(() -> {
                UserLessonProgress saved = userLessonProgressRepository.save(
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
        UserLessonProgress progress = userLessonProgressRepository
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

        return progressMapper.toResponse(userLessonProgressRepository.save(progress));
    }

    @Override
    @Transactional(readOnly = true)
    public LessonProgressResponse getProgress(UUID userId, UUID lessonId) {
        return progressMapper.toResponse(findByUserAndLesson(userId, lessonId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonProgressResponse> getUserProgress(UUID userId) {
        return userLessonProgressRepository
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
        return userLessonProgressRepository
            .findAllByUserIdAndStatus(userId, status)
            .stream()
            .map(progressMapper::toResponse)
            .toList();
    }

     private UserLessonProgress findByUserAndLesson(UUID userId, UUID lessonId) {
        return userLessonProgressRepository
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
        List<UserLessonProgress> all = userLessonProgressRepository
            .findAllByUserIdAndLessonIdIn(userId, lessonIds);

        return all.stream()
            .collect(Collectors.groupingBy(UserLessonProgress::getLessonId))
            .entrySet()
            .stream()
            .map(entry -> progressMapper.toSummary(entry.getKey(), entry.getValue()))
            .toList();
    }

    @Override
    @Transactional
    public StudyRecordResponse recordCard(UUID userId, UUID deckId,
                                          UUID cardId, StudyRecordRequest request) {
        log.info("Recording card: {} for user: {}, deck: {}, mark: {}",
                cardId, userId, deckId, request.mark());

        UserDeckProgress progress = userDeckProgressRepository
                .findByUserIdAndDeckId(userId, deckId)
                .orElseGet(() -> userDeckProgressRepository.save(
                        UserDeckProgress.builder()
                                .userId(userId)
                                .deckId(deckId)
                                .totalStudied(0)
                                .build()
                ));

        UserCardRecord record = cardRecordRepository
                .findByUserDeckProgressIdAndFlashCardId(progress.getId(), cardId)
                .map(existing -> {
                    existing.setMark(request.mark());
                    existing.setStudyCount(existing.getStudyCount() + 1);
                    existing.setStudiedAt(Instant.now());
                    return existing;
                })
                .orElseGet(() -> {
                    UserCardRecord newRecord = UserCardRecord.builder()
                            .flashCardId(cardId)
                            .mark(request.mark())
                            .studyCount(1)
                            .studiedAt(Instant.now())
                            .userDeckProgress(progress)
                            .build();
                    return cardRecordRepository.save(newRecord);
                });

        progress.setLastStudiedCardId(record.getFlashCardId().getLeastSignificantBits()); // hoặc keep UUID
        if (record.getStudyCount() == 1) {
            progress.setTotalStudied(progress.getTotalStudied() + 1);
        }

        return mapper.toStudyRecordResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public DeckProgressResponse getDeckProgress(UUID userId, UUID deckId) {
        log.debug("Getting deck progress for user: {}, deck: {}", userId, deckId);

        return userDeckProgressRepository
                .findWithCardsByUserIdAndDeckId(userId, deckId)
                .map(mapper::toDeckProgressResponse)
                .orElseGet(mapper::toEmptyDeckProgressResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeckStudiedResponse> getStudiedDecks(UUID userId, List<UUID> deckIds) {
        log.debug("Getting studied decks for user: {}, deckIds: {}", userId, deckIds);

        Set<UUID> studiedDeckIds = userDeckProgressRepository
                .findAllByUserIdAndDeckIdIn(userId, deckIds)
                .stream()
                .map(UserDeckProgress::getDeckId)
                .collect(Collectors.toSet());

        return deckIds.stream()
                .map(deckId -> DeckStudiedResponse.builder()
                        .deckId(deckId)
                        .started(studiedDeckIds.contains(deckId))
                        .build())
                .toList();
    }
}