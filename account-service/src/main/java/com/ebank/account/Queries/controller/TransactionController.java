// TransactionController.java - UPDATED with QueryGateway
package com.ebank.account.Queries.controller;

import com.ebank.account.Commands.dto.AddBeneficiaryRequestDTO;
import com.ebank.account.Commands.dto.ConfirmTransactionRequestDTO;
import com.ebank.account.Commands.dto.InitiateTransactionRequestDTO;
import com.ebank.account.Queries.dto.ApiResponse;
import com.ebank.account.Queries.dto.BeneficiaryResponseDTO;
import com.ebank.account.Queries.dto.PagedResponse;
import com.ebank.account.Queries.dto.TransactionResponseDTO;
import com.ebank.account.Queries.query.GetBeneficiariesByAccountIdQuery;
import com.ebank.account.Queries.query.GetBeneficiaryByIdQuery;
import com.ebank.account.Queries.query.GetTransactionByIdQuery;
import com.ebank.account.Queries.query.GetTransactionsByAccountIdQuery;
import com.ebank.account.Queries.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    private final QueryGateway queryGateway;
    
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> initiateTransaction(
            @Valid @RequestBody InitiateTransactionRequestDTO request) {
        
        TransactionResponseDTO transaction = transactionService.initiateTransaction(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<TransactionResponseDTO>builder()
                        .success(true)
                        .message("Transaction initiated. OTP sent to your email.")
                        .data(transaction)
                        .build());
    }
    
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> confirmTransaction(
            @Valid @RequestBody ConfirmTransactionRequestDTO request) {
        
        TransactionResponseDTO transaction = transactionService.confirmTransaction(request);
        
        return ResponseEntity.ok(ApiResponse.<TransactionResponseDTO>builder()
                .success(true)
                .message("Transaction completed successfully")
                .data(transaction)
                .build());
    }
    
    @GetMapping("/account/{accountId}")
    public CompletableFuture<ResponseEntity<PagedResponse<TransactionResponseDTO>>> getAccountTransactions(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        GetTransactionsByAccountIdQuery query = new GetTransactionsByAccountIdQuery(accountId, page, size);
        
        return queryGateway.query(query, PagedResponse.class)
                .thenApply(ResponseEntity::ok);
    }
    
    @GetMapping("/{transactionId}/account/{accountId}")
    public CompletableFuture<ResponseEntity<ApiResponse<TransactionResponseDTO>>> getTransactionById(
            @PathVariable UUID transactionId,
            @PathVariable UUID accountId) {
        
        GetTransactionByIdQuery query = new GetTransactionByIdQuery(transactionId, accountId);
        
        return queryGateway.query(query, TransactionResponseDTO.class)
                .thenApply(transaction -> ResponseEntity.ok(
                        ApiResponse.<TransactionResponseDTO>builder()
                                .success(true)
                                .data(transaction)
                                .build()
                ));
    }
    
    @PostMapping("/beneficiaries")
    public ResponseEntity<ApiResponse<BeneficiaryResponseDTO>> addBeneficiary(
            @Valid @RequestBody AddBeneficiaryRequestDTO request) {
        
        BeneficiaryResponseDTO beneficiary = transactionService.addBeneficiary(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BeneficiaryResponseDTO>builder()
                        .success(true)
                        .message("Beneficiary added successfully")
                        .data(beneficiary)
                        .build());
    }
    
    @GetMapping("/beneficiaries/account/{accountId}")
    public CompletableFuture<ResponseEntity<ApiResponse<List<BeneficiaryResponseDTO>>>> getAccountBeneficiaries(
            @PathVariable UUID accountId) {
        
        GetBeneficiariesByAccountIdQuery query = new GetBeneficiariesByAccountIdQuery(accountId);
        
        return queryGateway.query(query, List.class)
                .thenApply(beneficiaries -> ResponseEntity.ok(
                        ApiResponse.<List<BeneficiaryResponseDTO>>builder()
                                .success(true)
                                .data((List<BeneficiaryResponseDTO>) beneficiaries)
                                .build()
                ));
    }
    
    @GetMapping("/beneficiaries/{beneficiaryId}/account/{accountId}")
    public CompletableFuture<ResponseEntity<ApiResponse<BeneficiaryResponseDTO>>> getBeneficiaryById(
            @PathVariable UUID beneficiaryId,
            @PathVariable UUID accountId) {
        
        GetBeneficiaryByIdQuery query = new GetBeneficiaryByIdQuery(beneficiaryId, accountId);
        
        return queryGateway.query(query, BeneficiaryResponseDTO.class)
                .thenApply(beneficiary -> ResponseEntity.ok(
                        ApiResponse.<BeneficiaryResponseDTO>builder()
                                .success(true)
                                .data(beneficiary)
                                .build()
                ));
    }
    
    @DeleteMapping("/beneficiaries/{beneficiaryId}/account/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteBeneficiary(
            @PathVariable UUID beneficiaryId,
            @PathVariable UUID accountId) {
        
        transactionService.deleteBeneficiary(beneficiaryId, accountId);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Beneficiary deleted successfully")
                .build());
    }
}