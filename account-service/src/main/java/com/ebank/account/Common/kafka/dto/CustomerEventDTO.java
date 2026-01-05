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
    UUID userId;
    String keycloakUserId;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String address;
    String city;
    String country;
    String role;
}
