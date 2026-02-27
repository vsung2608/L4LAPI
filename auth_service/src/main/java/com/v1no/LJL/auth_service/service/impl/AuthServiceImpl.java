package com.v1no.LJL.auth_service.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.v1no.LJL.auth_service.exception.AccountDisabledException;
import com.v1no.LJL.auth_service.exception.AccountLockedException;
import com.v1no.LJL.auth_service.exception.DuplicateEmailException;
import com.v1no.LJL.auth_service.exception.InvalidTokenException;
import com.v1no.LJL.auth_service.model.dto.request.LoginRequest;
import com.v1no.LJL.auth_service.model.dto.request.RegisterRequest;
import com.v1no.LJL.auth_service.model.dto.response.AuthResponse;
import com.v1no.LJL.auth_service.model.entity.RefreshToken;
import com.v1no.LJL.auth_service.model.entity.UserCredential;
import com.v1no.LJL.auth_service.model.enums.Role;
import com.v1no.LJL.auth_service.repository.RefreshTokenRepository;
import com.v1no.LJL.auth_service.repository.UserCredentialRepository;
import com.v1no.LJL.auth_service.security.JwtService;
import com.v1no.LJL.auth_service.service.AuthService;
import com.v1no.LJL.common.event.VerificationEmailEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor   
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.backend-url}")
    private String backendUrl;

    public void register(RegisterRequest request) {
        if (userCredentialRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already exists");
        }
        
        String passwordHash = passwordEncoder.encode(request.password());

        String veificationToken = generateSecureCode();
        
        UserCredential user = UserCredential.builder()
            .email(request.email())
            .username(request.username())
            .passwordHash(passwordHash)
            .verificationToken(veificationToken)
            .emailVerified(false)
            .isActive(true)
            .isLocked(false)
            .role(Role.USER)
            .failedLoginCount(0)
            .build();
        
        userCredentialRepository.save(user);

        VerificationEmailEvent event = VerificationEmailEvent.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .linkVerification(buildVerificationLink(veificationToken))
            .build();

        kafkaTemplate.send("sent-verification-email", event);
    }

    public AuthResponse login(LoginRequest request) {
        UserCredential user = userCredentialRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        
        if (user.isAccountLocked()) {
            throw new AccountLockedException("Account is locked until " + user.getLockedUntil());
        }
        
        if (!user.getIsActive()) {
            throw new AccountDisabledException("Account is disabled");
        }
        
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.incrementFailedLoginCount();
            userCredentialRepository.save(user);
            
            throw new BadCredentialsException("Invalid credentials");
        }
        
        user.resetFailedLoginCount();
        user.setLastLoginAt(LocalDateTime.now());
        userCredentialRepository.save(user);
        
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(accessToken, refreshToken);
    }

    public void logout(String rawToken) {
        String tokenHash = hashToken(rawToken);
        
        RefreshToken refreshToken = refreshTokenRepository
            .findByTokenHash(tokenHash)
            .orElseThrow(() -> new InvalidTokenException("Token not found"));
        
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }

    private String hashToken(String rawToken) {
        return DigestUtils.sha256Hex(rawToken);
    }

    private String buildVerificationLink(String token) {
        return backendUrl + "/verify-email?token=" + token;
    }

    private String generateSecureCode(){
        String characters = "0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int charAt = random.nextInt(characters.length());
            code.append(characters.charAt(charAt));
        }
        return code.toString();
    }
}
