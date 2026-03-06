package com.v1no.LJL.auth_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;
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
import com.v1no.LJL.auth_service.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final HttpServletRequest request;
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

    public void logoutAllDevices(UUID userId) {
        List<RefreshToken> tokens = refreshTokenRepository
            .findByUserIdAndRevokedFalse(userId);
        
        tokens.forEach(RefreshToken::revoke);
        refreshTokenRepository.saveAll(tokens);
    }

    private String hashToken(String rawToken) {
        return DigestUtils.sha256Hex(rawToken);
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private String getBrowserInfo() {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null) return "Unknown";

        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";

        return "Unknown";
    }

    private String getOSInfo() {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null) return "Unknown";

        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "MacOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone")) return "iOS";

        return "Unknown";
    }

    private String getDeviceType() {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null) return "Unknown";

        if (userAgent.contains("Mobi")) return "Mobile";
        if (userAgent.contains("Tablet")) return "Tablet";

        return "Desktop";
    }
}
