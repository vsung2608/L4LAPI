package com.v1no.LJL.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.v1no.LJL.auth_service.model.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
