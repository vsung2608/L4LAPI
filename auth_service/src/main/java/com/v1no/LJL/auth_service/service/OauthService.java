package com.v1no.LJL.auth_service.service;

import com.v1no.LJL.auth_service.model.dto.response.AuthResponse;

public interface OauthService {
    AuthResponse loginWithGoogle(String authorizationCode);
}
