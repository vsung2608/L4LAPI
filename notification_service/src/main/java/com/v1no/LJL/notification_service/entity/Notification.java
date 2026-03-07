package com.v1no.LJL.notification_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @MongoId
    private String id;
    
    private String userId;
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