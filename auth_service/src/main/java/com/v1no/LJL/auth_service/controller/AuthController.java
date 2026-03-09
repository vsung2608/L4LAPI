package com.v1no.LJL.auth_service.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.v1no.LJL.auth_service.exception.InvalidTokenException;
import com.v1no.LJL.auth_service.model.dto.request.ForgotPasswordRequest;
import com.v1no.LJL.auth_service.model.dto.request.LoginRequest;
import com.v1no.LJL.auth_service.model.dto.request.RegisterRequest;
import com.v1no.LJL.auth_service.model.dto.request.ResetPasswordRequest;
import com.v1no.LJL.auth_service.model.dto.response.AccessTokenResponse;
import com.v1no.LJL.auth_service.model.dto.response.AuthResponse;
import com.v1no.LJL.auth_service.service.AuthService;
import com.v1no.LJL.auth_service.service.OauthService;
import com.v1no.LJL.auth_service.service.PasswordResetTokenService;
import com.v1no.LJL.auth_service.service.RefreshTokenService;
import com.v1no.LJL.common.dto.ApiResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final OauthService oauthService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.created("Registration successful. Please check your email for verification instructions."));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
        @RequestBody @Valid LoginRequest request,
        HttpServletResponse response
    ) {
        var auth = authService.login(request);
        Cookie refreshCookie = new Cookie("refresh_token", auth.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/v1/auth/refresh");
        refreshCookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(refreshCookie);
        return ResponseEntity.ok(new AccessTokenResponse(auth.accessToken()));
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<AccessTokenResponse> loginWithGoogle(
            @RequestParam String code,
            HttpServletResponse response) {

        AuthResponse auth = oauthService.loginWithGoogle(code);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", auth.refreshToken())
            .httpOnly(true)
            .secure(true)
            .path("/api/v1/auth/refresh")
            .maxAge(Duration.ofDays(30))
            .sameSite("Strict")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(new AccessTokenResponse(auth.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        authService.logout(token);
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        String redirectUrl = authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(redirectUrl))
            .build();
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
        @CookieValue(name = "refresh_token", required = false) String rawToken,
        HttpServletResponse response
    ) {

        if (rawToken == null) {
            throw new InvalidTokenException("Refresh token not found");
        }

        AuthResponse auth = refreshTokenService.refreshAccessToken(rawToken);

        Cookie refreshCookie = new Cookie("refresh_token", auth.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/v1/auth/refresh");
        refreshCookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new AccessTokenResponse(auth.accessToken()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request) {

        passwordResetTokenService.requestPasswordReset(request.email());

        return ResponseEntity.ok(Map.of(
            "message", "If this email exists, a reset link has been sent"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {

        passwordResetTokenService.resetPassword(request.token(), request.newPassword());

        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
