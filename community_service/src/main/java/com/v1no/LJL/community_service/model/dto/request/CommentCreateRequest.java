package com.v1no.LJL.community_service.model.dto.request;

import java.util.UUID;

import com.v1no.LJL.community_service.model.enums.CommentTargetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
    @NotNull(message = "Target type is required")
    CommentTargetType targetType,

    @NotNull(message = "Target ID is required")
    UUID targetId,

    UUID parentId,

    @NotBlank(message = "Content must not be blank")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    String content
) {
}
