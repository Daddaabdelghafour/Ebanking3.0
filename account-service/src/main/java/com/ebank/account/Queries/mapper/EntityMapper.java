// EntityMapper.java - Updated version with Transaction and Beneficiary mappings
package com.ebank.account.Queries.mapper;

import com.ebank.account.Queries.dto.AccountResponseDTO;
import com.ebank.account.Queries.dto.BeneficiaryResponseDTO;
import com.ebank.account.Queries.dto.OperationResponseDTO;
import com.ebank.account.Queries.dto.TransactionResponseDTO;
import com.ebank.account.Queries.entity.Account;
import com.ebank.account.Queries.entity.Beneficiary;
import com.ebank.account.Queries.entity.Operation;
import com.ebank.account.Queries.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    AccountResponseDTO toAccountResponseDTO(Account account);
    List<AccountResponseDTO> toAccountResponseDTOs(List<Account> accounts);
    
    OperationResponseDTO toOperationResponseDTO(Operation operation);
    List<OperationResponseDTO> toOperationResponseDTOs(List<Operation> operations);
    
    @Mapping(source = "sourceAccount.id", target = "sourceAccountId")
    @Mapping(source = "destinationAccount.id", target = "destinationAccountId")
    @Mapping(source = "sourceAccount.accountNumber", target = "sourceAccountNumber")
    @Mapping(source = "destinationAccount.accountNumber", target = "destinationAccountNumber")
    TransactionResponseDTO toTransactionResponseDTO(Transaction transaction);
    
    List<TransactionResponseDTO> toTransactionResponseDTOs(List<Transaction> transactions);
    
    BeneficiaryResponseDTO toBeneficiaryResponseDTO(Beneficiary beneficiary);
    List<BeneficiaryResponseDTO> toBeneficiaryResponseDTOs(List<Beneficiary> beneficiaries);
}