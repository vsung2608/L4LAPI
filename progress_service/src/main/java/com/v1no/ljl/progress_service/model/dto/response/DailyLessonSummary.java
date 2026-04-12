package com.v1no.ljl.progress_service.model.dto.response;

import java.time.LocalDate;

public interface DailyLessonSummary {
    LocalDate getStudyDate();
    Long getTotalLessons();
    Long getTotalDictation();
    Long getTotalShadowing();
}
