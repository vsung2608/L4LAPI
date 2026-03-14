package com.v1no.ljl.progress_service.model.dto.response;

import com.v1no.ljl.progress_service.model.enums.CardMark;
import java.util.UUID;

public record CardProgressResponse(
        UUID flashCardId,
        CardMark mark,
        Integer studyCount
) {}
