package com.v1no.LJL.auth_service.model.dto.response;

import java.time.LocalDateTime;

public record VipStatusResponse(
    
    Boolean isVip,
    Boolean isActive,
    Boolean isLifetime,
    String planCode,
    LocalDateTime startedAt,
    LocalDateTime expiresAt,
    Long daysRemaining,
    String status
) {
}