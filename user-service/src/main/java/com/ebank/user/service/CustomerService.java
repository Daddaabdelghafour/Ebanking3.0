package com.ebank.user.service;

import com.ebank.user.dto.CustomerCreatedEvent;
import com.ebank.user.dto.NotificationEvent;
import com.ebank.user.dto.UserRegisteredEvent;
import com.ebank.user.entity.Customer;
import com.ebank.user.mapper.CustomerMapper;
import com.ebank.user.repo.CustomerRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepo customerRepo;
    private final CustomerMapper customerMapper;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaProducerServiceNotification kafkaProducerServiceNotification;
    public CustomerService(CustomerRepo customerRepo, CustomerMapper customerMapper,KafkaProducerServiceNotification kafkaProducerServiceNotification , KafkaProducerService kafkaProducerService) {
        this.customerMapper = customerMapper;
        this.kafkaProducerService = kafkaProducerService;
        this.customerRepo = customerRepo;
        this.kafkaProducerServiceNotification=kafkaProducerServiceNotification;
    }

    public void createCustomer(UserRegisteredEvent userRegisteredEvent) {
        Customer customer = customerMapper.toEntity(userRegisteredEvent);
        customerRepo.save(customer);
        CustomerCreatedEvent event = new CustomerCreatedEvent(customer.getUserId());
        kafkaProducerService.sendCustomerCreatedEvent(event);
        NotificationEvent notificationEvent = new NotificationEvent(
                customer.getUserId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );
        kafkaProducerServiceNotification.sendCustomerCreatedEvent(notificationEvent);
    }
    @Transactional
    public void updateEmail(String oldEmail, String newEmail) {
        // 1️⃣ Chercher le client
        Customer customer = customerRepo.findByEmail(oldEmail);
        // 2️⃣ Mise à jour de l'email
        customer.setEmail(newEmail);
        // 3️⃣ Sauvegarde
        customerRepo.save(customer);
        // 4️⃣ Log (optionnel)
        System.out.println("Email updated in DB: " + oldEmail + " -> " + newEmail);
    }


}
