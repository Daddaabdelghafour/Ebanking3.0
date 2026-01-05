package com.ebank.account.Queries.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(boolean success,T data, String message, LocalDateTime timestamp) {
        return ApiResponse.<T>builder()
                .success(success)
                .message(message)
                .timestamp(timestamp)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(boolean success, String message, LocalDateTime timestamp) {
        return ApiResponse.<T>builder()
                .success(success)
                .message(message)
                .timestamp(timestamp)
                .build();
    }
}