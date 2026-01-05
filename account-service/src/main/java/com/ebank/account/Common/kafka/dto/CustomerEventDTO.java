package com.ebank.account.Common.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEventDTO {
    /**
     * Customer Service publishes this field as 'userId'.
     * In this service we usually refer to the same identifier as 'customerId'.
     */
    private UUID userId;

    private String email;

    /**
     * Must be CUSTOMER for this service to create an account.
     */
    private String role;
}
