package com.ebank.notification.controller;

import com.ebank.notification.dto.NotificationRequest;
import com.ebank.notification.dto.NotificationResponse;
import com.ebank.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        // TODO: Implement send notification manually
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable UUID userId) {
        // TODO: Implement get notifications by user ID
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable UUID id) {
        // TODO: Implement get notification by ID
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        // TODO: Implement mark notification as read
        return ResponseEntity.ok().build();
    }
}

