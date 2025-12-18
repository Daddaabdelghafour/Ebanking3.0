package com.ebank.account.service;

import com.ebank.account.dto.AccountCreateRequest;
import com.ebank.account.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    public List<AccountResponse> getAllAccounts() {
        // TODO: Implement get all accounts
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AccountResponse getAccountById(UUID id) {
        // TODO: Implement get account by ID
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<AccountResponse> getAccountsByCustomerId(UUID customerId) {
        // TODO: Implement get accounts by customer ID
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AccountResponse createAccount(AccountCreateRequest request) {
        // TODO: Implement create account
        // 1. Validate customer exists (call user-service)
        // 2. Create account entity
        // 3. Save to database
        // 4. Publish ACCOUNT_CREATED event to Kafka
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AccountResponse updateAccount(UUID id, AccountCreateRequest request) {
        // TODO: Implement update account
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AccountResponse deposit(UUID id, Double amount) {
        // TODO: Implement deposit
        // 1. Get account
        // 2. Update balance
        // 3. Create operation record
        // 4. Publish ACCOUNT_UPDATED event
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AccountResponse withdraw(UUID id, Double amount) {
        // TODO: Implement withdraw
        // 1. Get account
        // 2. Check balance
        // 3. Update balance
        // 4. Create operation record
        // 5. Publish ACCOUNT_UPDATED event
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Double getBalance(UUID id) {
        // TODO: Implement get balance
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

