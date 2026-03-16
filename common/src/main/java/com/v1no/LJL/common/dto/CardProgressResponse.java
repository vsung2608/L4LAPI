package com.v1no.LJL.common.dto;
import java.util.UUID;

public record CardProgressResponse(
        UUID flashCardId,
        String mark,
        Integer studyCount
) {}
