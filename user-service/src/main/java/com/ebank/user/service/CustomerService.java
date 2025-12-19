package com.ebank.user.service;

import org.example.customerservice.dto.CustomerCreatedEvent;
import org.example.customerservice.dto.CustomerDto;
import org.example.customerservice.entity.Customer;
import org.example.customerservice.mapper.CustomerMapper;
import org.example.customerservice.repo.CustomerRepo;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepo customerRepo;
    private final CustomerMapper  customerMapper;
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
