package com.v1no.LJL.auth_service.service.impl;

public class AuthServiceImpl implements AuthService {
    UserResponse register(RegistrationRequest request);

    AuthResponse authenticate(LoginRequest request);

    void logout(String token);

    AuthResponse refresh(RefreshTokenRequest refreshToken);

    void activationAccount(String emailToken);

    void sendValidationEmail(Users user);

    String generateAndSaveEmailToken(Users user);

    String generateActivationCode(int length);
}
