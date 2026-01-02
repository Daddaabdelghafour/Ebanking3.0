package com.ebank.account.Queries.repository;

import com.ebank.account.Queries.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("SELECT a FROM Account a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Account> findById(@Param("id") UUID id);
    
    @Query("SELECT a FROM Account a WHERE a.customerId = :customerId AND a.deletedAt IS NULL")
    Optional<Account> findByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT MAX(a.accountNumber) FROM Account a")
    String findMaxAccountNumber();
}
