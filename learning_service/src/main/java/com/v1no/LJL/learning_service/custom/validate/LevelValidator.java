package com.v1no.LJL.learning_service.custom.validate;

import org.springframework.stereotype.Component;

import com.v1no.LJL.learning_service.exception.BusinessException;
import com.v1no.LJL.learning_service.model.enums.LanguageCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LevelValidator {

    public void validate(String languageCode, String level) {
        LanguageCode lang;
        try {
            lang = LanguageCode.valueOf(languageCode);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Unsupported language code: " + languageCode);
        }

        boolean valid = lang.validLevels().contains(level);
        if (!valid) {
            throw new BusinessException(
                "Invalid level '%s' for language '%s'. Valid levels: %s"
                    .formatted(level, languageCode, lang.validLevels())
            );
        }
    }
}