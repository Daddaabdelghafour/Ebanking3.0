package com.ebank.account.Queries.service;

import com.ebank.account.Queries.dto.AccountResponseDTO;
import com.ebank.account.Queries.dto.OperationResponseDTO;
import com.ebank.account.Queries.dto.PagedResponse;
import com.ebank.account.Queries.entity.Account;
import com.ebank.account.Queries.entity.Operation;
import com.ebank.account.Queries.mapper.EntityMapper;
import com.ebank.account.Queries.query.*;
import com.ebank.account.Queries.repository.AccountRepository;
import com.ebank.account.Queries.repository.OperationRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountQueryHandlerService {

    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final EntityMapper entityMapper;

    public AccountQueryHandlerService(
            AccountRepository accountRepository,
            OperationRepository operationRepository,
            EntityMapper entityMapper) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.entityMapper = entityMapper;
    }

    @QueryHandler
    public AccountResponseDTO handle(@NotNull GetAccountByIdQuery query) {
        Account account = accountRepository
                .findById(query.accountId())
                .orElse(null);
        if (account == null) {
            return null;
        }

        return entityMapper.toAccountResponseDTO(account);
    }

    @QueryHandler
    public AccountResponseDTO handle(@NotNull GetAccountByCustomerIdQuery query) {
        Account account = accountRepository
                .findByCustomerId(query.customerId())
                .orElse(null);
        if (account == null) {
            return null;
        }

        return entityMapper.toAccountResponseDTO(account);
    }

    @QueryHandler
    public OperationResponseDTO handle(@NotNull GetOperationByIdQuery query) {
        Operation operation = operationRepository
                .findById(query.operationId())
                .orElse(null);
        if (operation == null) {
            return null;
        }

        return  entityMapper.toOperationResponseDTO(operation);
    }

    @QueryHandler
    public PagedResponse<OperationResponseDTO> handle(@NotNull GetOperationsByAccountId query) {
        Pageable pageable = PageRequest
                .of(query.page(), query.size());

        Page<Operation> operations = operationRepository
                .findByAccountId(query.accountId(), pageable);

        List<OperationResponseDTO> operationResponseDTOS = entityMapper.toOperationResponseDTOs(operations.getContent());

        return PagedResponse.<OperationResponseDTO>builder()
                .content(operationResponseDTOS)
                .pageNumber(operations.getNumber())
                .pageSize(operations.getSize())
                .totalElements(operations.getTotalElements())
                .totalPages(operations.getTotalPages())
                .last(operations.isLast())
                .first(operations.isFirst())
                .build();
    }
}
