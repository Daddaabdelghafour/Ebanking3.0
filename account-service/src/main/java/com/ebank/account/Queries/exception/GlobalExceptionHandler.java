package com.ebank.account.Queries.exception;

import com.ebank.account.Queries.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountCreationFailedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountCreationFailed(AccountCreationFailedException exception){
        ApiResponse<Object> response = ApiResponse.error(
                false,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountNotFound(AccountNotFoundException exception){
        ApiResponse<Object> response = ApiResponse.error(
                false,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(404)
                .body(response);
    }

    @ExceptionHandler(OperationNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleOperationNotFound(OperationNotFoundException exception){
        ApiResponse<Object> response = ApiResponse.error(
                false,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException exception){
        ApiResponse<Object> response = ApiResponse.error(
                false,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .badRequest()
                .body(response);
    }
}
