package com.v1no.LJL.learning_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.v1no.LJL.common.dto.ApiResponse;
import com.v1no.LJL.learning_service.model.dto.response.PronunciationResultResponse;
import com.v1no.LJL.learning_service.service.PronunciationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pronunciation")
public class PronunciationController {

    private final PronunciationService pronunciationService;

    @PostMapping("/assess")
    public ResponseEntity<ApiResponse<PronunciationResultResponse>> assess(
        @RequestParam("audio") MultipartFile audioFile,
        @RequestParam("referenceText") String referenceText,
        @RequestParam(value = "language", defaultValue = "en-US") String language
    ) throws Exception {
        
        byte[] audioData = audioFile.getBytes();
        PronunciationResultResponse result = pronunciationService.assess(audioData, referenceText, language);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
