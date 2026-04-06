package com.v1no.LJL.community_service.model.dto.request;

import com.v1no.LJL.community_service.model.entity.GroupMessage.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(

        @NotBlank(message = "Content must not be blank")
        @Size(max = 5000, message = "Content must not exceed 5000 characters")
        String content,

        String username,

        String avatarUrl,

        MessageType messageType
) {}
