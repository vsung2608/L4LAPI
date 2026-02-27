package com.v1no.LJL.learning_service.model.enums;

import java.util.Arrays;
import java.util.List;

public enum LanguageCode {
    ja, zh, en, ko;

    public List<String> validLevels() {
        return switch (this) {
            case ja -> Arrays.stream(JlptLevel.values()).map(Enum::name).toList();
            case zh -> Arrays.stream(HskLevel.values()).map(Enum::name).toList();
            case en -> Arrays.stream(CefrLevel.values()).map(Enum::name).toList();
            case ko -> Arrays.stream(TopikLevel.values()).map(Enum::name).toList();
        };
    }
}
  
