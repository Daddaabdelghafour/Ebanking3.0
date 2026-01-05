package com.exemple.apigateway.model;


import com.fasterxml.jackson. annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLog {

    private String requestId;
    private String method;
    private String path;
    private String userId;
    private int statusCode;
    private long duration; // en millisecondes

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String userAgent;
    private String ipAddress;
}
