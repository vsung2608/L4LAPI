package com.v1no.LJL.learning_service.model.dto.response;

import java.util.List;

public record LanguageCatalogResponse(
    String languageCode,
    List<CategoryWithLessonsResponse> categories
) {}
