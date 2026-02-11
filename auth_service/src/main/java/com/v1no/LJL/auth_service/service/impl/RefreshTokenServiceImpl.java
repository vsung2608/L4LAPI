package com.v1no.LJL.auth_service.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import com.v1no.LJL.auth_service.exception.InvalidTokenException;
import com.v1no.LJL.auth_service.model.dto.response.AuthResponse;
import com.v1no.LJL.auth_service.model.entity.RefreshToken;
import com.v1no.LJL.auth_service.model.entity.UserCredential;
import com.v1no.LJL.auth_service.repository.RefreshTokenRepository;
import com.v1no.LJL.auth_service.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl {
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(UserCredential user) {
        String rawToken = UUID.randomUUID().toString();
    
        String tokenHash = hashToken(rawToken);
    
        Map<String, String> deviceInfo = Map.of(
            "device", getDeviceType(),
            "browser", getBrowserInfo(),
            "os", getOSInfo(),
            "ip", getClientIP()
        );
        
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .tokenHash(tokenHash)
            .deviceInfo(deviceInfo)
            .expiresAt(LocalDateTime.now().plusDays(30))
            .revoked(false)
            .build();
        
        refreshTokenRepository.save(refreshToken);
        
        return rawToken;
    }
    
    private String hashToken(String rawToken) {
        return DigestUtils.sha256Hex(rawToken);
    }

    public AuthResponse refreshAccessToken(String rawToken) {
        String tokenHash = hashToken(rawToken);
        
        RefreshToken refreshToken = refreshTokenRepository
            .findByTokenHash(tokenHash)
            .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        
        if (!refreshToken.isValid()) {
            throw new InvalidTokenException("Refresh token is invalid");
        }

        String newAccessToken = jwtService.generateAccessToken(refreshToken.getUser());
        
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        
        String newRefreshToken = createRefreshToken(refreshToken.getUser());
        
        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
