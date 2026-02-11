package com.v1no.LJL.auth_service.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.v1no.LJL.auth_service.model.entity.UserCredential;
import com.v1no.LJL.auth_service.repository.UserCredentialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserCredentialRepository credentialRepository;
    @Override
    public UserCredential loadUserByUsername(String email) throws UsernameNotFoundException {
        return credentialRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}
