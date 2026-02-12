package com.v1no.LJL.notification_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "notifications")
@Data
@Builder
public class Notification {
    @Id
    private String id;
    
    private UUID userId;
    private String type;
    private String title;
    private String message;
    
    private Boolean sentViaEmail;
    private Boolean sentViaInApp;
    
    private Boolean isRead;
    private LocalDateTime readAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}