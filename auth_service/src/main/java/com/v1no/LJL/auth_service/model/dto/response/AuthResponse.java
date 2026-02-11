package com.v1no.LJL.auth_service.model.dto.response;

public record AuthResponse(
    String accessToken,
    String refreshToken
) {}
