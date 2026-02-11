package com.v1no.LJL.auth_service.model.dto.request;

public record LoginRequest(
    String email,
    String password
) {}
