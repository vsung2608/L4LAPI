package com.v1no.LJL.auth_service.service;

import java.util.UUID;

import com.v1no.LJL.auth_service.model.dto.response.AuthResponse;
import com.v1no.LJL.auth_service.model.entity.UserCredential;

public interface RefreshTokenService {
    String createRefreshToken(UserCredential user);
    AuthResponse refreshAccessToken(String rawToken);
    void logoutAllDevices(UUID userId);
}
