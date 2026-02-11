package com.v1no.LJL.auth_service.exception;

public class AccountDisabledException extends RuntimeException {
    public AccountDisabledException(String message) {
        super(message);
    }
    
}
