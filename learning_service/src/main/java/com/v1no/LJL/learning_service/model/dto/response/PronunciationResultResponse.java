package com.v1no.LJL.learning_service.model.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PronunciationResultResponse {
    private String recognizedText;

    private double pronScore;

    private double accuracyScore;

    private double fluencyScore;

    private double completenessScore;

    private double prosodyScore;

    private List<WordDetailResponse> words;
}
