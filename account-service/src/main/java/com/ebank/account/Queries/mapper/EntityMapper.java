package com.ebank.account.Queries.mapper;

import com.ebank.account.Queries.dto.AccountResponseDTO;
import com.ebank.account.Queries.dto.OperationResponseDTO;
import com.ebank.account.Queries.entity.Account;
import com.ebank.account.Queries.entity.Operation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    AccountResponseDTO toAccountResponseDTO(Account account);
    List<AccountResponseDTO> toAccountResponseDTOs(List<Account> accounts);
    OperationResponseDTO toOperationResponseDTO(Operation operation);
    List<OperationResponseDTO> toOperationResponseDTOs(List<Operation> operations);
}
