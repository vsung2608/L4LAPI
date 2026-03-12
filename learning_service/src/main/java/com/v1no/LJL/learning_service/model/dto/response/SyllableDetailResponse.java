package com.v1no.LJL.learning_service.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SyllableDetailResponse {
    private String syllable;

    private double accuracyScore;

    private long offsetMs;

    private long durationMs;
}
