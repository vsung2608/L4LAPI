package com.v1no.LJL.auth_service.service;

public interface PasswordResetTokenService {
    public void requestPasswordReset(String email);
    public void resetPassword(String rawToken, String newPassword);
}
