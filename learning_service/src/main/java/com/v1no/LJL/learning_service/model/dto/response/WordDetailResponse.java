package com.v1no.LJL.learning_service.model.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WordDetailResponse {
    private String word;

    private double accuracyScore;

    private String errorType;

    private List<SyllableDetailResponse> syllables;

    // Chi tiết từng âm vị (phoneme) trong từ
    private List<PhonemeDetailResponse> phonemes;
}
