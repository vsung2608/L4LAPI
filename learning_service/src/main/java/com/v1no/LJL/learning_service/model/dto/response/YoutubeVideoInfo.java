package com.v1no.LJL.learning_service.model.dto.response;

public record YoutubeVideoInfo(
    String title,
    String thumbnailUrl,
    int durationSeconds
) {}