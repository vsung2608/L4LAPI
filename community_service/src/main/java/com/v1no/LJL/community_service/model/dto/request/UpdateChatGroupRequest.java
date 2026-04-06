package com.v1no.LJL.community_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateChatGroupRequest(

        @NotBlank(message = "Group name must not be blank")
        @Size(min = 2, max = 200, message = "Group name must be between 2 and 200 characters")
        String name

) {}
