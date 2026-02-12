package com.v1no.LJL.notification_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.v1no.LJL.notification_service.entity.Notification;
import com.v1no.LJL.notification_service.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    
    public void createNotification(UUID userId, String type, String title, String message, boolean sendEmail) {
        Notification notification = Notification.builder()
            .userId(userId)
            .type(type)
            .title(title)
            .message(message)
            .sentViaInApp(true)
            .sentViaEmail(sendEmail)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(90))
            .build();
        
        notificationRepository.save(notification);
    }
    
    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}