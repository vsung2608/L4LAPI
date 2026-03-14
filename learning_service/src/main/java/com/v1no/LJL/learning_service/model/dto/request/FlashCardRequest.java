// FlashCardRequest.java
package com.v1no.LJL.learning_service.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FlashCardRequest(

        @NotBlank(message = "Content must not be blank")
        String content,

        MultipartFile thumbnail,

        @NotBlank(message = "Transcription must not be blank")
        String transcription,

        @NotBlank(message = "Translation must not be blank")
        String translation,

        @NotBlank(message = "Example in language must not be blank")
        String exampleLang,

        @NotBlank(message = "Example in Vietnamese must not be blank")
        String exampleViet,

        @NotBlank(message = "Explanation in language must not be blank")
        String explaintLang,

        @NotBlank(message = "Explanation in Vietnamese must not be blank")
        String explaintViet,

        String hint,

        String difficulty,

        @NotNull(message = "Display order must not be null")
        Integer displayOrder
) {}