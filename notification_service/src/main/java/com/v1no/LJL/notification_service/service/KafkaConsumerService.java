package com.v1no.LJL.notification_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.v1no.LJL.common.event.ForgotPasswordEmailEvent;
import com.v1no.LJL.common.event.VerificationEmailEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final EmailService emailService;
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "sent-verification-email", groupId = "notification-service-group")
    public void handleVerificationEmail(VerificationEmailEvent event) {
        notificationService.createNotification(
            event.getUserId(),
            "VERIFICATION_EMAIL_SENT",
            "Email xác minh đã được gửi!",
            "Chúng tôi đã gửi email xác minh đến địa chỉ của bạn.",
            true
        );

        emailService.sendVerificationEmail(
            event.getEmail(),
            event.getLinkVerification()
        );
    }

    @KafkaListener(topics = "forgot-password-email", groupId = "notification-service-group")
    public void handleForgotPasswordEmail(ForgotPasswordEmailEvent event) {
        notificationService.createNotification(
            event.getUserId(),
            "FORGOT_PASSWORD_EMAIL_SENT",
            "Email khôi phục mật khẩu đã được gửi!",
            "Chúng tôi đã gửi email khôi phục mật khẩu đến địa chỉ của bạn.",
            true
        );

        emailService.sendPasswordResetEmail(
            event.getEmail(),
            event.getToken()
        );
    }
}
