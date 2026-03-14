package com.v1no.ljl.progress_service.mapper;

import com.v1no.ljl.progress_service.model.dto.response.CardProgressResponse;
import com.v1no.ljl.progress_service.model.dto.response.DeckProgressResponse;
import com.v1no.ljl.progress_service.model.dto.response.StudyRecordResponse;
import com.v1no.ljl.progress_service.model.entity.UserCardRecord;
import com.v1no.ljl.progress_service.model.entity.UserDeckProgress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudyProgressMapper {

    public DeckProgressResponse toDeckProgressResponse(UserDeckProgress progress) {
        List<CardProgressResponse> records = progress.getCards().stream()
                .map(r -> new CardProgressResponse(
                        r.getFlashCardId(),
                        r.getMark(),
                        r.getStudyCount()
                ))
                .toList();

        return DeckProgressResponse.builder()
                .started(true)
                .lastStudiedCardId(progress.getLastStudiedCardId())
                .records(records)
                .build();
    }

    public DeckProgressResponse toEmptyDeckProgressResponse() {
        return DeckProgressResponse.builder()
                .started(false)
                .lastStudiedCardId(null)
                .records(List.of())
                .build();
    }

    public StudyRecordResponse toStudyRecordResponse(UserCardRecord record) {
        return StudyRecordResponse.builder()
                .flashCardId(record.getFlashCardId())
                .mark(record.getMark())
                .studyCount(record.getStudyCount())
                .studiedAt(record.getStudiedAt())
                .build();
    }
}