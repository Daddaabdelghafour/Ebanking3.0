// TransactionRepository.java
package com.ebank.account.Queries.repository;

import com.ebank.account.Common.enums.TransactionStatus;
import com.ebank.account.Queries.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountId(@Param("accountId") UUID accountId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = :status")
    List<Transaction> findByStatus(@Param("status") TransactionStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE t.id = :id AND t.sourceAccount.id = :accountId")
    Optional<Transaction> findByIdAndSourceAccountId(@Param("id") UUID id, @Param("accountId") UUID accountId);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = 'OTP_SENT' AND t.otpExpiryTime < :currentTime")
    List<Transaction> findExpiredOtpTransactions(@Param("currentTime") LocalDateTime currentTime);
}