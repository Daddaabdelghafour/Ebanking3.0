package com.exemple.authservice.exception;

import com.exemple.authservice.model.response.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<AuthResponse> handleEmailNotVerified(EmailNotVerifiedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(AuthResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(Map.of("email", ex.getEmail(), "errorCode", "EMAIL_NOT_VERIFIED"))
                        .build());
    }
}

