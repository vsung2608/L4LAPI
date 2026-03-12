package com.v1no.LJL.learning_service.mapper;

import org.springframework.stereotype.Component;

import com.v1no.LJL.learning_service.model.dto.request.CreateSentenceRequest;
import com.v1no.LJL.learning_service.model.dto.request.UpdateSentenceRequest;
import com.v1no.LJL.learning_service.model.dto.response.SentenceResponse;
import com.v1no.LJL.learning_service.model.entity.Lesson;
import com.v1no.LJL.learning_service.model.entity.Sentence;

@Component
public class SentenceMapper {

    public Sentence toEntity(CreateSentenceRequest request, Lesson lesson) {
        return Sentence.builder()
            .lesson(lesson)
            .content(request.content())
            .transcription(request.transcription())
            .translation(request.translation())
            .startTimeSeconds(request.startTimeSeconds())
            .endTimeSeconds(request.endTimeSeconds())
            .orderIndex(request.orderIndex())
            .build();
    }

    public void updateEntity(Sentence sentence, UpdateSentenceRequest request) {
        sentence.setContent(request.content());
        sentence.setTranscription(request.furigana());
        sentence.setTranslation(request.translation());
        sentence.setStartTimeSeconds(request.startTimeSeconds());
        sentence.setEndTimeSeconds(request.endTimeSeconds());
        sentence.setOrderIndex(request.orderIndex());
    }

    public SentenceResponse toResponse(Sentence sentence) {
        return new SentenceResponse(
            sentence.getId(),
            sentence.getLesson().getId(),
            sentence.getContent(),
            sentence.getTranscription(),
            sentence.getTranslation(),
            sentence.getStartTimeSeconds(),
            sentence.getEndTimeSeconds(),
            sentence.getOrderIndex(),
            sentence.getCreatedAt(),
            sentence.getUpdatedAt()
        );
    }
}
