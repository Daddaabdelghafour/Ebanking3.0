package com.ebank.payment.controller;

import com.ebank.payment.dto.PaymentRequest;
import com.ebank.payment.dto.PaymentResponse;
import com.ebank.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        // TODO: Implement payment processing
        // 1. Validate accounts exist (call account-service)
        // 2. Check source account balance (call account-service)
        // 3. Process payment
        // 4. Update account balances (call account-service)
        // 5. Create transaction record
        // 6. Publish PAYMENT_COMPLETED or PAYMENT_FAILED event to Kafka
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        // TODO: Implement get payment by ID
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByAccountId(@PathVariable UUID accountId) {
        // TODO: Implement get payments by account ID
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable UUID id) {
        // TODO: Implement payment cancellation
        // Reverse transaction if possible
        // Publish PAYMENT_CANCELLED event
        return ResponseEntity.ok().build();
    }
}

