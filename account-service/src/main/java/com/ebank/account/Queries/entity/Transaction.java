// Transaction.java
package com.ebank.account.Queries.entity;

import com.ebank.account.Common.enums.TransactionStatus;
import com.ebank.account.Common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id", nullable = false)
    private Account destinationAccount;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(length = 500)
    private String failureReason;

    @Column(length = 200)
    private String reference;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(length = 6)
    private String otpCode;

    private LocalDateTime otpExpiryTime;

    private Boolean otpVerified;
}