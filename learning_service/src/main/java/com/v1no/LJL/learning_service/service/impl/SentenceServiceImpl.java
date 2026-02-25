package com.v1no.LJL.learning_service.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.learning_service.exception.BusinessException;
import com.v1no.LJL.learning_service.exception.DuplicateResourceException;
import com.v1no.LJL.learning_service.exception.ResourceNotFoundException;
import com.v1no.LJL.learning_service.mapper.SentenceMapper;
import com.v1no.LJL.learning_service.model.dto.request.CreateSentenceRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateSentenceRequest;
import com.v1no.LJL.learning_service.model.dto.response.SentenceResponse;
import com.v1no.LJL.learning_service.model.entity.Lesson;
import com.v1no.LJL.learning_service.model.entity.Sentence;
import com.v1no.LJL.learning_service.repository.LessonRepository;
import com.v1no.LJL.learning_service.repository.SentenceRepository;
import com.v1no.LJL.learning_service.service.SentenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SentenceServiceImpl implements SentenceService {

    private final SentenceRepository sentenceRepository;
    private final LessonRepository lessonRepository;
    private final SentenceMapper sentenceMapper;

    @Override
    public SentenceResponse create(CreateSentenceRequest request) {
        log.info("Creating sentence: lessonId={}, orderIndex={}", request.lessonId(), request.orderIndex());

        Lesson lesson = findLessonById(request.lessonId());
        validateTimestamps(request.startTimeSeconds(), request.endTimeSeconds());

        boolean orderExists = sentenceRepository.existsByLessonIdAndOrderIndex(
            request.lessonId(), request.orderIndex()
        );
        if (orderExists) {
            throw new DuplicateResourceException(
                "Order index " + request.orderIndex() + " already exists in lesson: " + request.lessonId()
            );
        }

        Sentence saved = sentenceRepository.save(sentenceMapper.toEntity(request, lesson));
        log.info("Sentence created: id={}", saved.getId());
        return sentenceMapper.toResponse(saved);
    }

    @Override
    public SentenceResponse update(UUID id, UpdateSentenceRequest request) {
        log.info("Updating sentence: id={}", id);

        Sentence sentence = findSentenceById(id);
        validateTimestamps(request.startTimeSeconds(), request.endTimeSeconds());

        boolean orderConflict = sentenceRepository.existsByLessonIdAndOrderIndexAndIdNot(
            sentence.getLesson().getId(), request.orderIndex(), id
        );
        if (orderConflict) {
            throw new DuplicateResourceException(
                "Order index " + request.orderIndex() + " already exists in this lesson"
            );
        }

        sentenceMapper.updateEntity(sentence, request);
        Sentence saved = sentenceRepository.save(sentence);

        log.info("Sentence updated: id={}", saved.getId());
        return sentenceMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting sentence: id={}", id);
        Sentence sentence = findSentenceById(id);
        sentenceRepository.delete(sentence);
        log.info("Sentence deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public SentenceResponse findById(UUID id) {
        Sentence sentence = sentenceRepository.findByIdWithLesson(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sentence not found: " + id));
        return sentenceMapper.toResponse(sentence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SentenceResponse> findByLessonId(UUID lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new ResourceNotFoundException("Lesson not found: " + lessonId);
        }
        return sentenceRepository.findAllByLessonIdOrdered(lessonId)
            .stream()
            .map(sentenceMapper::toResponse)
            .toList();
    }

    private Sentence findSentenceById(UUID id) {
        return sentenceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sentence not found: " + id));
    }

    private Lesson findLessonById(UUID id) {
        return lessonRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + id));
    }

    private void validateTimestamps(Integer start, Integer end) {
        if (start != null && end != null && end <= start) {
            throw new BusinessException("End time must be greater than start time");
        }
    }
}
