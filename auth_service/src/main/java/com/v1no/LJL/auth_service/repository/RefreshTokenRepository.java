package com.v1no.LJL.auth_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.v1no.LJL.auth_service.model.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    
    List<RefreshToken> findByUserIdAndRevokedFalse(UUID userId);
}
