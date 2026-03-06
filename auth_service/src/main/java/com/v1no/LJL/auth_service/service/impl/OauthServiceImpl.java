package com.v1no.LJL.auth_service.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.auth_service.client.GoogleOAuthClient;
import com.v1no.LJL.auth_service.exception.OAuthException;
import com.v1no.LJL.auth_service.model.dto.response.AuthResponse;
import com.v1no.LJL.auth_service.model.dto.response.GoogleTokenResponse;
import com.v1no.LJL.auth_service.model.dto.response.GoogleUserInfo;
import com.v1no.LJL.auth_service.model.entity.OAuthConnection;
import com.v1no.LJL.auth_service.model.entity.OAuthConnection.OAuthProvider;
import com.v1no.LJL.auth_service.model.enums.Role;
import com.v1no.LJL.auth_service.model.entity.UserCredential;
import com.v1no.LJL.auth_service.repository.OauthConnectionRepository;
import com.v1no.LJL.auth_service.repository.UserCredentialRepository;
import com.v1no.LJL.auth_service.security.JwtService;
import com.v1no.LJL.auth_service.service.OauthService;
import com.v1no.LJL.auth_service.service.RefreshTokenService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthServiceImpl implements OauthService {
    
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserCredentialRepository userRepository;
    private final OauthConnectionRepository oauthRepository;
    private final GoogleOAuthClient googleClient;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.oauth.client-id}")
    private String clientId;
    
    @Value("${google.oauth.client-secret}")
    private String clientSecret;
    
    @Value("${google.oauth.redirect-uri}")
    private String redirectUri;
    
    @Value("${google.oauth.grant-type:authorization_code}")
    private String grantType;
    
    @Override
    @Transactional
    public AuthResponse loginWithGoogle(String authorizationCode) {
        log.info("Starting Google OAuth login process");
        if (authorizationCode == null || authorizationCode.isBlank()) {
            throw new IllegalArgumentException("Authorization code is required");
        }
        
        try {
            log.debug("Exchanging authorization code for access token");
            GoogleTokenResponse tokenResponse = googleClient.exchangeCodeForToken(
                authorizationCode, 
                clientId, 
                clientSecret, 
                redirectUri, 
                grantType
            );
            
            log.debug("Fetching user info from Google");
            GoogleUserInfo googleUser = googleClient.getUserInfo(
                "Bearer " + tokenResponse.getAccessToken()
            );
            
            log.info("Google user authenticated: email={}, id={}", 
                googleUser.getEmail(), googleUser.getId());
            
            UserCredential user = findOrCreateUser(googleUser, tokenResponse);
            
            log.debug("Generating JWT tokens for user: {}", user.getUsername());
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(user);
            
            log.info("Google OAuth login successful for user: {}", user.getUsername());
            
            return new AuthResponse(
                accessToken,
                refreshToken
            );
            
        } catch (FeignException.Unauthorized e) {
            log.error("Invalid authorization code or expired", e);
            throw new OAuthException("Invalid authorization code", e);
            
        } catch (FeignException e) {
            log.error("Failed to communicate with Google OAuth API", e);
            throw new OAuthException("Failed to authenticate with Google", e);
            
        } catch (Exception e) {
            log.error("Unexpected error during Google OAuth login", e);
            throw new OAuthException("OAuth login failed", e);
        }
    }
    
    private UserCredential findOrCreateUser(
        GoogleUserInfo googleUser, 
        GoogleTokenResponse tokenResponse
    ) {
        Optional<OAuthConnection> existingConnection = oauthRepository
            .findByProviderAndProviderUserId(
                OAuthProvider.GOOGLE, 
                googleUser.getId()
            );
        
        if (existingConnection.isPresent()) {
            log.debug("Found existing OAuth connection for Google user: {}", googleUser.getId());
            
            OAuthConnection connection = existingConnection.get();
            updateOAuthConnection(connection, tokenResponse, googleUser);
            
            return connection.getUser();
        }
        
        Optional<UserCredential> existingUser = userRepository
            .findByUsername(googleUser.getEmail());
        
        UserCredential user;
        
        if (existingUser.isPresent()) {
            user = existingUser.get();
            log.info("Linking Google account to existing user: {}", user.getUsername());
        } else {
            user = createUserFromOAuth(googleUser);
            log.info("Created new user from Google account: {}", user.getUsername());
        }
        
        createOAuthConnection(user, tokenResponse, googleUser);
        
        return user;
    }
    
    /**
     * Create new user from Google OAuth
     */
    private UserCredential createUserFromOAuth(GoogleUserInfo googleUser) {
        String username = generateUsername(googleUser.getEmail());
        String randomPassword = generateRandomPassword();
        
        UserCredential user = UserCredential.builder()
            .username(googleUser.getEmail())
            .username(username)
            .passwordHash(passwordEncoder.encode(randomPassword))
            .emailVerified(googleUser.getVerifiedEmail())
            .isActive(true)
            .isLocked(false)
            .role(Role.USER)
            .failedLoginCount(0)
            .build();
        
        return userRepository.save(user);
    }
    
    /**
     * Create OAuth connection record
     */
    private void createOAuthConnection(
        UserCredential user,
        GoogleTokenResponse tokenResponse,
        GoogleUserInfo googleUser
    ) {
        OAuthConnection connection = OAuthConnection.builder()
            .user(user)
            .provider(OAuthProvider.GOOGLE)
            .providerUserId(googleUser.getId())
            .accessToken(tokenResponse.getAccessToken())
            .refreshToken(tokenResponse.getRefreshToken())
            .expiresAt(calculateExpiryTime(tokenResponse.getExpiresIn()))
            .profileData(Map.of(
                "id", googleUser.getId(),
                "name", googleUser.getName(),
                "given_name", googleUser.getGivenName() != null ? googleUser.getGivenName() : "",
                "family_name", googleUser.getFamilyName() != null ? googleUser.getFamilyName() : "",
                "email", googleUser.getEmail(),
                "picture", googleUser.getPicture() != null ? googleUser.getPicture() : "",
                "locale", googleUser.getLocale() != null ? googleUser.getLocale() : "vi",
                "verified_email", googleUser.getVerifiedEmail()
            ))
            .build();
        
        oauthRepository.save(connection);
        log.debug("Created OAuth connection for user: {}", user.getUsername());
    }
    
    /**
     * Update existing OAuth connection with new tokens
     */
    private void updateOAuthConnection(
        OAuthConnection connection,
        GoogleTokenResponse tokenResponse,
        GoogleUserInfo googleUser
    ) {
        connection.setAccessToken(tokenResponse.getAccessToken());
        
        if (tokenResponse.getRefreshToken() != null) {
            connection.setRefreshToken(tokenResponse.getRefreshToken());
        }
        
        connection.setExpiresAt(calculateExpiryTime(tokenResponse.getExpiresIn()));
        
        connection.setProfileData(Map.of(
            "id", googleUser.getId(),
            "name", googleUser.getName(),
            "given_name", googleUser.getGivenName() != null ? googleUser.getGivenName() : "",
            "family_name", googleUser.getFamilyName() != null ? googleUser.getFamilyName() : "",
            "email", googleUser.getEmail(),
            "picture", googleUser.getPicture() != null ? googleUser.getPicture() : "",
            "locale", googleUser.getLocale() != null ? googleUser.getLocale() : "vi",
            "verified_email", googleUser.getVerifiedEmail()
        ));
        
        oauthRepository.save(connection);
        log.debug("Updated OAuth connection tokens for user: {}", connection.getUser().getUsername());
    }
    
    /**
     * Generate unique username from email
     */
    private String generateUsername(String email) {
        String base = email.split("@")[0];
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        return base + "_" + randomSuffix;
    }
    
    /**
     * Generate random password for OAuth users
     * OAuth users don't need to know this password
     */
    private String generateRandomPassword() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }
    
    /**
     * Calculate token expiry time
     */
    private LocalDateTime calculateExpiryTime(Integer expiresIn) {
        if (expiresIn == null || expiresIn <= 0) {
            return LocalDateTime.now().plusHours(1);
        }
        return LocalDateTime.now().plusSeconds(expiresIn);
    }
}