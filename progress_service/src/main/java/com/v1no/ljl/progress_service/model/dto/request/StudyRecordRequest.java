package com.v1no.ljl.progress_service.model.dto.request;

import com.v1no.ljl.progress_service.model.enums.CardMark;
import jakarta.validation.constraints.NotNull;

public record StudyRecordRequest(

        @NotNull(message = "Mark must not be null")
        CardMark mark
) {}
