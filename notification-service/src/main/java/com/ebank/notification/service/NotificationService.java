package com.ebank.notification.service;

import com.ebank.notification.dto.NotificationRequest;
import com.ebank.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    public NotificationResponse sendNotification(NotificationRequest request) {
        // TODO: Implement send notification
        // 1. Create notification entity
        // 2. Send via appropriate channel (email, SMS, push)
        // 3. Update status
        // 4. Save to database
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<NotificationResponse> getNotificationsByUserId(UUID userId) {
        // TODO: Implement get notifications by user ID
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public NotificationResponse getNotificationById(UUID id) {
        // TODO: Implement get notification by ID
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public NotificationResponse markAsRead(UUID id) {
        // TODO: Implement mark as read
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void sendEmail(String to, String subject, String body) {
        // TODO: Implement email sending
    }

    private void sendSMS(String phoneNumber, String message) {
        // TODO: Implement SMS sending (e.g., Twilio)
    }

    private void sendPushNotification(UUID userId, String message) {
        // TODO: Implement push notification
    }
}

