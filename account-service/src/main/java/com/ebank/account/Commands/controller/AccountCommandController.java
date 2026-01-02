package com.ebank.account.Commands.controller;

import com.ebank.account.Commands.command.*;
import com.ebank.account.Commands.util.factory.CommandFactory;
import com.ebank.account.Common.enums.AccountStatus;
import com.ebank.account.Common.kafka.dto.AccountEventDTO;
import com.ebank.account.Common.kafka.producer.AccountEventProducer;
import com.ebank.account.Queries.dto.ApiResponse;
import com.ebank.account.Commands.dto.*;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/accounts/commands")
public class AccountCommandController {

        private final CommandGateway commandGateway;
        private final AccountEventProducer accountEventProducer;

        public AccountCommandController(CommandGateway commandGateway,
                                        AccountEventProducer accountEventProducer
        ) {
                this.commandGateway = commandGateway;
                this.accountEventProducer = accountEventProducer;
        }

        @PostMapping
        public CompletableFuture<ResponseEntity<ApiResponse<UUID>>> createAccount(
                        @RequestBody @Valid AccountRequestDTO dto) {
                CreateAccountCommand command = CommandFactory.createAccountCommand(dto);

                return commandGateway.send(command)
                                .thenApply(result -> ResponseEntity
                                                .status(HttpStatus.CREATED)
                                                .body(ApiResponse.success(
                                                                true,
                                                                (UUID) result,
                                                                "Account created successfully",
                                                                LocalDateTime.now())))
                                .exceptionally(ex -> ResponseEntity
                                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body(ApiResponse.error(
                                                                false,
                                                                "Failed to create account: " + ex.getMessage(),
                                                                LocalDateTime.now())));
        }

        @PostMapping("/{id}/credit")
        public CompletableFuture<ResponseEntity<ApiResponse<String>>> creditAccount(
                        @PathVariable UUID id,
                        @RequestBody @Valid OperationRequestDTO dto) {
                CreditAccountCommand command = CommandFactory.creditAccountCommand(id, dto);

                return commandGateway.send(command)
                                .thenApply(result -> ResponseEntity
                                                .ok(ApiResponse.success(
                                                                true,
                                                                "Account credited successfully",
                                                                "Account credited successfully",
                                                                LocalDateTime.now())))
                                .exceptionally(ex -> ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(ApiResponse.error(
                                                                false,
                                                                "Failed to credit account: " + ex.getMessage(),
                                                                LocalDateTime.now())));
        }

        @PostMapping("/{id}/debit")
        public CompletableFuture<ResponseEntity<ApiResponse<String>>> debitAccount(
                        @PathVariable UUID id,
                        @RequestBody @Valid OperationRequestDTO dto) {
                DebitAccountCommand command = CommandFactory.debitAccountCommand(id, dto);

                return commandGateway.send(command)
                                .thenApply(result -> ResponseEntity
                                                .ok(ApiResponse.success(
                                                                true,
                                                                "Account debited successfully",
                                                                "Account debited successfully",
                                                                LocalDateTime.now())))
                                .exceptionally(ex -> ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(ApiResponse.error(
                                                                false,
                                                                "Failed to debit account: " + ex.getMessage(),
                                                                LocalDateTime.now())));
        }

        @PutMapping("/{id}/status")
        public CompletableFuture<ResponseEntity<ApiResponse<String>>> updateAccountStatus(
                        @PathVariable UUID id,
                        @RequestBody @Valid UpdateAccountStatusRequestDTO dto) {
                BaseCommand<?> command = dto.accountStatus() == AccountStatus.ACTIVATED
                                ? CommandFactory.activateAccountCommand(id)
                                : CommandFactory.suspendAccountCommand(id);

                return commandGateway.send(command)
                                .thenApply(result -> ResponseEntity
                                                .ok(ApiResponse.success(
                                                                true,
                                                                "Account status updated successfully",
                                                                "Account status updated successfully",
                                                                LocalDateTime.now())))
                                .exceptionally(ex -> ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(ApiResponse.error(
                                                                false,
                                                                "Failed to update account status: " + ex.getMessage(),
                                                                LocalDateTime.now())));
        }

        @DeleteMapping("/{id}")
        public CompletableFuture<ResponseEntity<ApiResponse<String>>> deleteAccount(
                        @PathVariable UUID id) {
                DeleteAccountCommand command = CommandFactory.deleteAccountCommand(id);

                return commandGateway.send(command)
                                .thenApply(result -> ResponseEntity
                                                .ok(ApiResponse.success(
                                                                true,
                                                                "Account deleted successfully",
                                                                "Account deleted successfully",
                                                                LocalDateTime.now())))
                                .exceptionally(ex -> ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(ApiResponse.error(
                                                                false,
                                                                "Failed to delete account: " + ex.getMessage(),
                                                                LocalDateTime.now())));
        }

        @PostMapping("/transfer")
        public CompletableFuture<ResponseEntity<ApiResponse<String>>> transferFunds(
                        @RequestBody @Valid TransferRequestDTO dto) {
                // Debit source account
                DebitAccountCommand debitCommand = CommandFactory.debitAccountCommand(
                                dto.sourceAccount(),
                                new OperationRequestDTO(dto.sourceAccount(), dto.amount(),
                                                "Transfer to " + dto.destinationAccount()));

                // Credit destination account
                CreditAccountCommand creditCommand = CommandFactory.creditAccountCommand(
                                dto.destinationAccount(),
                                new OperationRequestDTO(dto.destinationAccount(), dto.amount(),
                                                "Transfer from " + dto.sourceAccount()));

                return commandGateway.send(debitCommand)
                                .thenCompose(debitResult -> commandGateway.send(creditCommand))
                                .thenApply(result -> ResponseEntity
                                                .ok(ApiResponse.success(
                                                                true,
                                                                "Transfer completed successfully",
                                                                "Transfer completed successfully",
                                                                LocalDateTime.now())))
                                .exceptionally(ex -> ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(ApiResponse.error(
                                                                false,
                                                                "Failed to transfer funds: " + ex.getMessage(),
                                                                LocalDateTime.now())));
        }

        @GetMapping("/test-kafka")
        public ResponseEntity<String> testKafkaConnection() {
                try {
                        AccountEventDTO testEvent = AccountEventDTO.builder()
                                        .accountId(UUID.randomUUID())
                                        .customerId(UUID.randomUUID())
                                        .email("test@example.com")
                                        .eventType("TEST_EVENT")
                                        .timestamp(LocalDateTime.now())
                                        .build();

                        accountEventProducer.publishAccountCreated(testEvent);
                        return ResponseEntity.ok("Kafka test message sent successfully!");
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Kafka test failed: " + e.getMessage());
                }
        }
}