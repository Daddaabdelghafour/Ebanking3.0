package com.ebank.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ebank.user.Enum.CustomerStatus;
import com.ebank.user.Enum.Gender;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "utilisateur",indexes = {
        @Index(columnList = "userId", unique = true),

})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID userId;
    @Column(nullable = false)
    @NotBlank(message = "CIN or Passport is required")
    private String cinOrPassport;
    @NotBlank(message = "Nationality is required")
    private String nationality;
    // ============================================================
    // PERSONAL INFORMATION
    // ============================================================
    @Column(nullable = false)
    @NotBlank(message = "First name is required")
    private String firstName;
    private String role;
    @Column(nullable = false)
    @NotBlank(message = "LastName name is required")
    private String lastName;

    @Column(nullable = false)
    @NotNull(message = "Birth date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Gender is required")
    private Gender gender;

    @Column(length = 100)
    private String profession;

    // ============================================================
    // CONTACT INFORMATION
    // ============================================================
    @Column(unique = true, nullable = false)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;
    private String address;
    private String city;
    private String country;
    // ============================================================
    // ACCOUNT STATUS (IMPROVED)
    // ============================================================
    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus = CustomerStatus.PENDING_VERIFICATION;
    private boolean isVerified;
    @CreationTimestamp
    private LocalDate createdAt;
    @UpdateTimestamp
    private LocalDate updatedAt;
}
