package com.v1no.LJL.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.v1no.LJL.auth_service.model.entity.UserCredential;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByEmail(String email);
    Optional<UserCredential> findByUsername(String username);
    boolean existsByEmail(String email);
}
