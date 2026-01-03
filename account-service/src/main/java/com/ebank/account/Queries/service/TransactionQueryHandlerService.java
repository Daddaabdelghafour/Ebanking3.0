// TransactionQueryHandlerService.java
package com.ebank.account.Queries.service;

import com.ebank.account.Queries.dto.BeneficiaryResponseDTO;
import com.ebank.account.Queries.dto.PagedResponse;
import com.ebank.account.Queries.dto.TransactionResponseDTO;
import com.ebank.account.Queries.entity.Beneficiary;
import com.ebank.account.Queries.entity.Transaction;
import com.ebank.account.Queries.mapper.EntityMapper;
import com.ebank.account.Queries.query.GetBeneficiariesByAccountIdQuery;
import com.ebank.account.Queries.query.GetBeneficiaryByIdQuery;
import com.ebank.account.Queries.query.GetTransactionByIdQuery;
import com.ebank.account.Queries.query.GetTransactionsByAccountIdQuery;
import com.ebank.account.Queries.repository.BeneficiaryRepository;
import com.ebank.account.Queries.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionQueryHandlerService {

    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final EntityMapper entityMapper;

    @QueryHandler
    public PagedResponse<TransactionResponseDTO> handle(GetTransactionsByAccountIdQuery query) {
        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Transaction> transactionPage = transactionRepository.findByAccountId(
                query.accountId(),
                pageable
        );

        List<TransactionResponseDTO> transactions = transactionPage
                .getContent()
                .stream()
                .map(entityMapper::toTransactionResponseDTO)
                .toList();

        return PagedResponse.<TransactionResponseDTO>builder()
                .content(transactions)
                .page(transactionPage.getNumber())
                .size(transactionPage.getSize())
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .last(transactionPage.isLast())
                .build();
    }

    @QueryHandler
    public TransactionResponseDTO handle(GetTransactionByIdQuery query) {
        Transaction transaction = transactionRepository
                .findByIdAndSourceAccountId(query.transactionId(), query.accountId())
                .orElse(null);

        if (transaction == null) {
            return null;
        }

        return entityMapper.toTransactionResponseDTO(transaction);
    }

    @QueryHandler
    public List<BeneficiaryResponseDTO> handle(GetBeneficiariesByAccountIdQuery query) {
        List<Beneficiary> beneficiaries = beneficiaryRepository.findByAccountId(query.accountId());
        return entityMapper.toBeneficiaryResponseDTOs(beneficiaries);
    }

    @QueryHandler
    public BeneficiaryResponseDTO handle(GetBeneficiaryByIdQuery query) {
        Beneficiary beneficiary = beneficiaryRepository
                .findByIdAndAccountId(query.beneficiaryId(), query.accountId())
                .orElse(null);

        if (beneficiary == null) {
            return null;
        }

        return entityMapper.toBeneficiaryResponseDTO(beneficiary);
    }
}