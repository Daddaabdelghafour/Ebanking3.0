// TransactionController.java
package com.ebank.account.Queries.controller;

import com.ebank.account.Commands.dto.AddBeneficiaryRequestDTO;
import com.ebank.account.Commands.dto.ConfirmTransactionRequestDTO;
import com.ebank.account.Commands.dto.InitiateTransactionRequestDTO;
import com.ebank.account.Queries.dto.ApiResponse;
import com.ebank.account.Queries.dto.BeneficiaryResponseDTO;
import com.ebank.account.Queries.dto.PagedResponse;
import com.ebank.account.Queries.dto.TransactionResponseDTO;
import com.ebank.account.Queries.exception.AccountNotFoundException;
import com.ebank.account.Queries.query.GetBeneficiariesByAccountIdQuery;
import com.ebank.account.Queries.query.GetBeneficiaryByIdQuery;
import com.ebank.account.Queries.query.GetTransactionByIdQuery;
import com.ebank.account.Queries.query.GetTransactionsByAccountIdQuery;
import com.ebank.account.Queries.service.TransactionQueryHandlerService;
import com.ebank.account.Queries.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final QueryGateway queryGateway;
    private final TransactionQueryHandlerService queryHandlerService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> initiateTransaction(
            @Valid @RequestBody InitiateTransactionRequestDTO request) {

        TransactionResponseDTO transaction = transactionService.initiateTransaction(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<TransactionResponseDTO>builder()
                        .success(true)
                        .message("Transaction initiated. OTP sent to your email.")
                        .data(transaction)
                        .timestamp(LocalDateTime.now())
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
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponseDTO>>> getAccountTransactions(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            GetTransactionsByAccountIdQuery query = new GetTransactionsByAccountIdQuery(accountId, page, size);
            PagedResponse<TransactionResponseDTO> transactions = queryHandlerService.handle(query);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            true,
                            transactions,
                            "Transactions retrieved successfully",
                            LocalDateTime.now()
                    )
            );
        } catch (AccountNotFoundException ex) {
            return ResponseEntity
                    .status(404)
                    .body(
                            ApiResponse.error(
                                    false,
                                    ex.getMessage(),
                                    LocalDateTime.now()
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResponse.error(
                                    false,
                                    "An error occurred while retrieving transactions: " + e.getMessage(),
                                    LocalDateTime.now()
                            )
                    );
        }
    }

    @GetMapping("/{transactionId}/account/{accountId}")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> getTransactionById(
            @PathVariable UUID transactionId,
            @PathVariable UUID accountId) {
        try {
            GetTransactionByIdQuery query = new GetTransactionByIdQuery(transactionId, accountId);
            TransactionResponseDTO transaction = queryHandlerService.handle(query);

            if (transaction == null) {
                return ResponseEntity
                        .status(404)
                        .body(
                                ApiResponse.error(
                                        false,
                                        "Transaction not found",
                                        LocalDateTime.now()
                                )
                        );
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            true,
                            transaction,
                            "Transaction retrieved successfully",
                            LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResponse.error(
                                    false,
                                    "An error occurred while retrieving transaction: " + e.getMessage(),
                                    LocalDateTime.now()
                            )
                    );
        }
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
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @GetMapping("/beneficiaries/account/{accountId}")
    public ResponseEntity<ApiResponse<List<BeneficiaryResponseDTO>>> getAccountBeneficiaries(
            @PathVariable UUID accountId) {
        try {
            GetBeneficiariesByAccountIdQuery query = new GetBeneficiariesByAccountIdQuery(accountId);
            List<BeneficiaryResponseDTO> beneficiaries = queryHandlerService.handle(query);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            true,
                            beneficiaries,
                            "Beneficiaries retrieved successfully",
                            LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResponse.error(
                                    false,
                                    "An error occurred while retrieving beneficiaries: " + e.getMessage(),
                                    LocalDateTime.now()
                            )
                    );
        }
    }

    @GetMapping("/beneficiaries/{beneficiaryId}/account/{accountId}")
    public ResponseEntity<ApiResponse<BeneficiaryResponseDTO>> getBeneficiaryById(
            @PathVariable UUID beneficiaryId,
            @PathVariable UUID accountId) {
        try {
            GetBeneficiaryByIdQuery query = new GetBeneficiaryByIdQuery(beneficiaryId, accountId);
            BeneficiaryResponseDTO beneficiary = queryHandlerService.handle(query);

            if (beneficiary == null) {
                return ResponseEntity
                        .status(404)
                        .body(
                                ApiResponse.error(
                                        false,
                                        "Beneficiary not found",
                                        LocalDateTime.now()
                                )
                        );
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            true,
                            beneficiary,
                            "Beneficiary retrieved successfully",
                            LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResponse.error(
                                    false,
                                    "An error occurred while retrieving beneficiary: " + e.getMessage(),
                                    LocalDateTime.now()
                            )
                    );
        }
    }

    @DeleteMapping("/beneficiaries/{beneficiaryId}/account/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteBeneficiary(
            @PathVariable UUID beneficiaryId,
            @PathVariable UUID accountId) {
        try {
            transactionService.deleteBeneficiary(beneficiaryId, accountId);

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Beneficiary deleted successfully")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResponse.error(
                                    false,
                                    "An error occurred while deleting beneficiary: " + e.getMessage(),
                                    LocalDateTime.now()
                            )
                    );
        }
    }
}