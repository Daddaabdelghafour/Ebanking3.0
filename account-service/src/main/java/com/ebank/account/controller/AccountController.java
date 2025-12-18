package com.ebank.account.controller;

import com.ebank.account.dto.AccountCreateRequest;
import com.ebank.account.dto.AccountResponse;
import com.ebank.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        // TODO: Implement get all accounts
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable UUID id) {
        // TODO: Implement get account by ID
        return ResponseEntity.ok().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomerId(@PathVariable UUID customerId) {
        // TODO: Implement get accounts by customer ID
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        // TODO: Implement create account
        // Validate customer exists via user-service
        // Create account entity
        // Save to database
        // Publish ACCOUNT_CREATED event to Kafka
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable UUID id, @Valid @RequestBody AccountCreateRequest request) {
        // TODO: Implement update account
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable UUID id, @RequestParam Double amount) {
        // TODO: Implement deposit
        // Update balance
        // Create operation record
        // Publish ACCOUNT_UPDATED event
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable UUID id, @RequestParam Double amount) {
        // TODO: Implement withdraw
        // Check balance
        // Update balance
        // Create operation record
        // Publish ACCOUNT_UPDATED event
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable UUID id) {
        // TODO: Implement get balance
        return ResponseEntity.ok(0.0);
    }
}

