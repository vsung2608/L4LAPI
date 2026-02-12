package com.v1no.LJL.auth_service.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.v1no.LJL.auth_service.exception.InvalidTokenException;
import com.v1no.LJL.auth_service.model.entity.PasswordResetToken;
import com.v1no.LJL.auth_service.model.entity.RefreshToken;
import com.v1no.LJL.auth_service.model.entity.UserCredential;
import com.v1no.LJL.auth_service.repository.PasswordResetTokenRepository;
import com.v1no.LJL.auth_service.repository.UserCredentialRepository;
import com.v1no.LJL.auth_service.service.RefreshTokenService;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl {
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final UserCredentialRepository userCredentialRepository;

    public void requestPasswordReset(String email) {
        UserCredential user = userCredentialRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Email not found"));
        
        String rawToken = generateSecureCode();
        String tokenHash = hashToken(rawToken);
        
        PasswordResetToken resetToken = PasswordResetToken.builder()
            .user(user)
            .tokenHash(tokenHash)
            .expiresAt(LocalDateTime.now().plusHours(1)) // 1 hour
            .used(false)
            .build();
        
        resetTokenRepository.save(resetToken);
        
        String resetLink = buildResetLink(rawToken);
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
        
        UserCredential user = resetToken.getUser();

        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(newPasswordHash);
        userCredentialRepository.save(user);
      
        resetToken.markAsUsed();
        resetTokenRepository.save(resetToken);
        
        refreshTokenService.logoutAllDevices(user.getId());
        
        // 8. Send notification email
    }

    public String generateSecureCode(){
        String characters = "0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int charAt = random.nextInt(characters.length());
            code.append(characters.charAt(charAt));
        }
        return code.toString();
    }

    private String hashToken(String rawToken) {
        return DigestUtils.sha256Hex(rawToken);
    }
}
