package com.v1no.LJL.auth_service.model.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateProfileRequest(
    
    @Size(max = 100, message = "Display name must not exceed 100 characters")
    String displayName,
    
    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    String avatarUrl,
    
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    String bio,
    
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,
    
    String gender,
    
    String studyGoal,
    
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Invalid phone number format")
    @Size(max = 20)
    String phoneNumber,
    
    @Size(max = 100)
    String facebook,
    
    @Size(max = 100)
    String instagram,
    
    @Size(max = 100)
    String twitter,
    
    @Size(max = 100)
    String country,
    
    @Size(max = 100)
    String city
) {
}