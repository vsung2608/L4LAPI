package com.v1no.LJL.auth_service.model.dto.request;

public record RegisterRequest(
        String email,
        String username,
        String password
) { }
