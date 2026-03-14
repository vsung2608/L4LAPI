package com.v1no.LJL.learning_service.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.v1no.LJL.learning_service.model.enums.LanguageCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CardDeckRequest(

        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        MultipartFile thumbnail,

        String description,

        @NotNull(message = "Language must not be null")
        LanguageCode language
) {}