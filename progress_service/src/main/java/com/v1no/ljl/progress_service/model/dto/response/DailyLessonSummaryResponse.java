package com.v1no.ljl.progress_service.model.dto.response;

import java.time.LocalDate;

public record DailyLessonSummaryResponse(
    LocalDate studyDate,
    Long totalLessons,
    Long totalDictation,
    Long totalShadowing
) {}