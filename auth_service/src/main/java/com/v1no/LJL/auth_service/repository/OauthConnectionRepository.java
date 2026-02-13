package com.v1no.LJL.auth_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.v1no.LJL.auth_service.model.entity.OAuthConnection;

public interface OauthConnectionRepository extends JpaRepository<OAuthConnection, UUID> {
    Optional<OAuthConnection> findByProviderAndProviderUserId(
        OAuthConnection.OAuthProvider provider, 
        String providerUserId
    );
}
