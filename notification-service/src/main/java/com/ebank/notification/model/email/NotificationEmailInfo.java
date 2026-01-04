package com.ebank.notification.model.email;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class NotificationEmailInfo {
    private String to;
    private String subject;
    private String templateName;
    private String message;
    private Map<String, Object> metadata; 
}