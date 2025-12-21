package com.ebank.user.mapper;

import com.ebank.user.dto.CustomerDto;
import com.ebank.user.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDto toDto(Customer customer);
    
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "profession", ignore = true)
    @Mapping(target = "customerStatus", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer toEntity(CustomerDto customerDto);
}
