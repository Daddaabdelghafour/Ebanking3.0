package com.ebank.user.service;

import com.ebank.user.dto.CustomerCreatedEvent;
import com.ebank.user.dto.CustomerDto;
import com.ebank.user.entity.Customer;
import com.ebank.user.mapper.CustomerMapper;
import com.ebank.user.repo.CustomerRepo;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepo customerRepo;
    private final CustomerMapper customerMapper;
    private final KafkaProducerService kafkaProducerService;
    
    public CustomerService(CustomerRepo customerRepo, CustomerMapper customerMapper, KafkaProducerService kafkaProducerService) {
        this.customerMapper = customerMapper;
        this.kafkaProducerService = kafkaProducerService;
        this.customerRepo = customerRepo;
    }
    
    public void createCustomer(CustomerDto customerDto) {
        Customer customer = customerMapper.toEntity(customerDto);
        customerRepo.save(customer);
        CustomerCreatedEvent event = new CustomerCreatedEvent(customer.getUserId());
        kafkaProducerService.sendCustomerCreatedEvent(event);
    }
}
