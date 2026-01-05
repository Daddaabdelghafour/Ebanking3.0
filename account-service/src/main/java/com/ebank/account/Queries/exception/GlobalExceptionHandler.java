// GlobalExceptionHandler.java - Updated
package com.ebank.account.Queries.exception;

import com.ebank.account.Commands.exception.BeneficiaryAlreadyExistsException;
import com.ebank.account.Commands.exception.InsufficientBalanceException;
import com.ebank.account.Commands.exception.InvalidOtpException;
import com.ebank.account.Queries.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
                .status(HttpStatus.NOT_FOUND)
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

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleTransactionNotFound(TransactionNotFoundException exception){
        ApiResponse<Object> response = ApiResponse.error(
                false,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(BeneficiaryNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleBeneficiaryNotFound(BeneficiaryNotFoundException exception){
        ApiResponse<Object> response = ApiResponse.error(
                false,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(BeneficiaryAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBeneficiaryAlreadyExists(BeneficiaryAlreadyExistsException exception){
        ApiResponse<Object> response = ApiResponse.error(
                false,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<Object>> handleInsufficientBalance(InsufficientBalanceException exception){
        ApiResponse<Object> response = ApiResponse.error(
                false,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidOtp(InvalidOtpException exception){
        ApiResponse<Object> response = ApiResponse.error(
                false,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
}