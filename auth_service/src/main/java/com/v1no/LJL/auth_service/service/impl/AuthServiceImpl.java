package com.v1no.LJL.auth_service.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import javax.security.auth.login.AccountLockedException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.v1no.LJL.auth_service.exception.AccountDisabledException;
import com.v1no.LJL.auth_service.exception.DuplicateEmailException;
import com.v1no.LJL.auth_service.exception.InvalidTokenException;
import com.v1no.LJL.auth_service.model.dto.request.LoginRequest;
import com.v1no.LJL.auth_service.model.dto.request.RegisterRequest;
import com.v1no.LJL.auth_service.model.dto.response.AuthResponse;
import com.v1no.LJL.auth_service.model.entity.PasswordResetToken;
import com.v1no.LJL.auth_service.model.entity.RefreshToken;
import com.v1no.LJL.auth_service.model.entity.UserCredential;
import com.v1no.LJL.auth_service.repository.RefreshTokenRepository;
import com.v1no.LJL.auth_service.repository.UserCredentialRepository;
import com.v1no.LJL.auth_service.security.JwtService;
import com.v1no.LJL.auth_service.service.AuthService;

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

    public void register(RegisterRequest request) {
        if (userCredentialRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already exists");
        }
        
        String passwordHash = passwordEncoder.encode(request.password());
        
        UserCredential user = UserCredential.builder()
            .email(request.email())
            .username(request.username())
            .passwordHash(passwordHash)
            .emailVerified(false)
            .isActive(true)
            .isLocked(false)
            .failedLoginCount(0)
            .build();
        
        userCredentialRepository.save(user);
        
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

    public void requestPasswordReset(String email) {
        // 1. Tìm user
        UserCredential user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Email not found"));
        
        // 2. Generate random token
        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);
        
        // 3. Tạo reset token record
        PasswordResetToken resetToken = PasswordResetToken.builder()
            .user(user)
            .tokenHash(tokenHash)
            .expiresAt(LocalDateTime.now().plusHours(1)) // 1 hour
            .used(false)
            .build();
        
        resetTokenRepository.save(resetToken);
        
        // 4. Gửi email
        String resetLink = buildResetLink(rawToken);
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }
    
    private String buildResetLink(String token) {
        return "https://app.nihongo.com/reset-password?token=" + token;
    }

    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken = resetTokenRepository
            .findByTokenHash(tokenHash)
            .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));
        
        if (!resetToken.isValid()) {
            throw new InvalidTokenException("Token is expired or already used");
        }
        
        // 4. Get user
        UserCredential user = resetToken.getUser();
        
        // 5. Update password
        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
        
        // 6. Mark token as used
        resetToken.markAsUsed();
        resetTokenRepository.save(resetToken);
        
        // 7. Revoke all refresh tokens (force logout all devices)
        refreshTokenService.logoutAllDevices(user.getId());
        
        // 8. Send notification email
        emailService.sendPasswordChangedNotification(user.getEmail());
    }

    private String hashToken(String rawToken) {
        return DigestUtils.sha256Hex(rawToken);
    }
}
