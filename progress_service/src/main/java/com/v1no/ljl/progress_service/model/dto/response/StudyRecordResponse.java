package com.v1no.ljl.progress_service.model.dto.response;

import com.v1no.ljl.progress_service.model.enums.CardMark;
import lombok.Builder;
import java.time.Instant;
import java.util.UUID;

@Builder
public record StudyRecordResponse(
        UUID flashCardId,
        CardMark mark,
        Integer studyCount,
        Instant studiedAt
) {}
