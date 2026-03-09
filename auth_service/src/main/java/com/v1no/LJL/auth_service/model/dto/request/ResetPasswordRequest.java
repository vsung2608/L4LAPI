package com.v1no.LJL.auth_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @NotBlank
    String token,

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    String newPassword
) {}