package com.ebank.account.Queries.controller;

import com.ebank.account.Queries.dto.AccountResponseDTO;
import com.ebank.account.Queries.dto.ApiResponse;
import com.ebank.account.Queries.dto.PagedResponse;
import com.ebank.account.Queries.dto.OperationResponseDTO;
import com.ebank.account.Queries.exception.AccountNotFoundException;
import com.ebank.account.Queries.exception.OperationNotFoundException;
import com.ebank.account.Queries.query.GetAccountByCustomerIdQuery;
import com.ebank.account.Queries.query.GetAccountByIdQuery;
import com.ebank.account.Queries.query.GetAllAccountsQuery;
import com.ebank.account.Queries.query.GetOperationByIdQuery;
import com.ebank.account.Queries.query.GetOperationsByAccountId;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/accounts/queries")
public class AccountQueryRestController {
    private final QueryGateway queryGateway;

    public AccountQueryRestController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> getAccount(
            @PathVariable UUID id
    ) {
        try {
            GetAccountByIdQuery query = new GetAccountByIdQuery(id);
            ResponseType<AccountResponseDTO> responseType = ResponseTypes
                    .instanceOf(AccountResponseDTO.class);
            AccountResponseDTO account = queryGateway
                    .query(query, responseType)
                    .join();

            if (account == null) {
                throw new AccountNotFoundException(
                        String.format("Account with id %s not found", id));
            }

            return ResponseEntity
                    .ok(
                            ApiResponse
                                    .success(
                                            true,
                                            account,
                                            "Account retrieved successfully",
                                            LocalDateTime.now()
                                    )
                    );
        } catch (AccountNotFoundException e) {
            return ResponseEntity
                    .status(404)
                    .body(
                            ApiResponse
                                    .error(
                                            false,
                                            e.getMessage(),
                                            LocalDateTime.now()
                                    )
                    );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResponse
                                    .error(
                                            false,
                                            "An error occurred while retrieving the account",
                                            LocalDateTime.now()
                                    )
                    );
        }
    }

    @GetMapping("/account/customer/{id}")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> getAccountByCustomer(
            @PathVariable UUID id
    ) {
        try {
            GetAccountByCustomerIdQuery query = new GetAccountByCustomerIdQuery(id);
            ResponseType<AccountResponseDTO> responseType = ResponseTypes
                    .instanceOf(AccountResponseDTO.class);
            AccountResponseDTO account = queryGateway
                    .query(query, responseType)
                    .join();

            if (account == null) {
                throw new AccountNotFoundException(
                        String.format("Account with id %s not found", id));
            }

            return ResponseEntity
                    .ok(
                            ApiResponse
                                    .success(
                                            true,
                                            account,
                                            "Account retrieved successfully",
                                            LocalDateTime.now()
                                    )
                    );
        } catch (AccountNotFoundException e) {
            return ResponseEntity
                    .status(404)
                    .body(
                            ApiResponse
                                    .error(
                                            false,
                                            e.getMessage(),
                                            LocalDateTime.now()
                                    )
                    );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResponse
                                    .error(
                                            false,
                                            "An error occurred while retrieving the account",
                                            LocalDateTime.now()
                                    )
                    );
        }
    }

    @GetMapping("/operation/{id}")
    public ResponseEntity<ApiResponse<OperationResponseDTO>> getOperation(
            @PathVariable UUID id
    ) {
        try {
            GetOperationByIdQuery query = new GetOperationByIdQuery(id);
            ResponseType<OperationResponseDTO> responseType = ResponseTypes
                    .instanceOf(OperationResponseDTO.class);
            OperationResponseDTO operation = queryGateway
                    .query(query, responseType)
                    .join();

            if (operation == null) {
                throw new OperationNotFoundException(
                        String.format("Operation with id %s not found", id)
                );
            }
            return ResponseEntity
                    .ok(
                            ApiResponse
                                    .success(
                                            true,
                                            operation,
                                            "Operation retrieved successfully",
                                            LocalDateTime.now()
                                    )
                    );
        } catch (OperationNotFoundException ex) {
            return ResponseEntity
                    .status(404)
                    .body(
                            ApiResponse
                                    .error(
                                            false,
                                            ex.getMessage(),
                                            LocalDateTime.now()
                                    )
                    );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResponse
                                    .error(
                                            false,
                                            "An error occurred while retrieving the operation",
                                            LocalDateTime.now()
                                    )
                    );
        }
    }

    @GetMapping("operations")
    public ResponseEntity<ApiResponse<PagedResponse<OperationResponseDTO>>> getOperations(
            @RequestParam(name = "accountId") UUID accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            GetOperationsByAccountId query = new GetOperationsByAccountId(accountId, page, size);

            PagedResponse<OperationResponseDTO> operations = queryGateway
                    .query(query, new ResponseType<PagedResponse<OperationResponseDTO>>() {
                        @Override
                        public boolean matches(Type responseType) {
                            return false;
                        }

                        @Override
                        public Class<PagedResponse<OperationResponseDTO>> responseMessagePayloadType() {
                            return null;
                        }

                        @Override
                        public Class<?> getExpectedResponseType() {
                            return null;
                        }
                    })
                    .join();

            return ResponseEntity
                    .ok(
                            ApiResponse.success(
                                    true,
                                    operations,
                                    "Operations retrieved successfully",
                                    LocalDateTime.now()
                            )
                    );
        } catch (OperationNotFoundException ex) {
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
                                    "An error occurred while retrieving the operations",
                                    LocalDateTime.now()
                            )
                    );
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AccountResponseDTO>>> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            GetAllAccountsQuery query = new GetAllAccountsQuery(page, size);
            ResponseType<PagedResponse<AccountResponseDTO>> responseType = new ResponseType<PagedResponse<AccountResponseDTO>>() {
                @Override
                public boolean matches(Type responseType) {
                    return false;
                }

                @Override
                public Class<PagedResponse<AccountResponseDTO>> responseMessagePayloadType() {
                    return null;
                }

                @Override
                public Class<?> getExpectedResponseType() {
                    return null;
                }
            };
            
            PagedResponse<AccountResponseDTO> accounts = queryGateway
                    .query(query, responseType)
                    .join();

            return ResponseEntity
                    .ok(
                            ApiResponse.success(
                                    true,
                                    accounts,
                                    "Accounts retrieved successfully",
                                    LocalDateTime.now()
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResponse.error(
                                    false,
                                    "An error occurred while retrieving accounts",
                                    LocalDateTime.now()
                            )
                    );
        }
    }
}
