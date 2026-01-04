package com.ebank.notification.model.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthEmailInfo {
    private String to;
    private String userName;
    private String code;
    private String validity;
    
    // Métadonnées pour le service
    private final String templateName;
    private final String subject;
}