package com.ebank.user.controller;

import com.ebank.user.service.CustomerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

//    @PostMapping
//    public ResponseEntity<String> createCustomer(@RequestBody CustomerDto customerDto) {
//        customerService.createCustomer(customerDto);
//        return ResponseEntity.ok("Customer created successfully");
//    }
}
