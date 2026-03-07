package com.v1no.LJL.notification_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v1no.LJL.common.event.ForgotPasswordEmailEvent;
import com.v1no.LJL.common.event.VerificationEmailEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "sent-verification-email", groupId = "notification-service-group")
    public void handleVerificationEmail(String json) {
        try {
            log.info("=== Kafka message received: {}", json); 
            VerificationEmailEvent event = objectMapper.readValue(json, VerificationEmailEvent.class);
            notificationService.createNotification(
                event.getUserId().toString(),
                "VERIFICATION_EMAIL_SENT",
                "Email xác minh đã được gửi!",
                "Chúng tôi đã gửi email xác minh đến địa chỉ của bạn.",
                true
            );

            emailService.sendVerificationEmail(
                event.getEmail(),
                event.getLinkVerification()
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "forgot-password-email", groupId = "notification-service-group")
    public void handleForgotPasswordEmail(String json) {
        try{
            ForgotPasswordEmailEvent event = objectMapper.readValue(json, ForgotPasswordEmailEvent.class);
            notificationService.createNotification(
                event.getUserId().toString(),
                "FORGOT_PASSWORD_EMAIL_SENT",
                "Email khôi phục mật khẩu đã được gửi!",
                "Chúng tôi đã gửi email khôi phục mật khẩu đến địa chỉ của bạn.",
                true
            );

            emailService.sendPasswordResetEmail(
                event.getEmail(),
                event.getToken()
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
